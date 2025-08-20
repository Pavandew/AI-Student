package com.ourmindai.aistudent.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ourmindai.aistudent.Constants
import com.ourmindai.aistudent.model.ChatHistoryModel
import com.ourmindai.aistudent.model.MessageModel
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.ourmindai.aistudent.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

class GeminiViewModel : ViewModel() {
    val key = BuildConfig.GEMINI_API_KEY
    init {
        try {
            Log.d("KEY", "API Key: $key")
        } catch (e: Exception) {
            Log.e("KEY", "Failed to load BuildConfig key", e)
        }
    }

    // UI message list with unique IDs for stable updates
    val messageList = mutableStateListOf<MessageModel>()

    // Track loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // Track error state
    private val _apiError = MutableStateFlow<String?>(null)
    val apiError = _apiError.asStateFlow()

    // Rate limiting variables
    private var lastRequestTime = 0L
    private val minRequestInterval = 1500L // 1.5 seconds between requests

    // Unique Session Id for fetching history with timeStamp
    var currentChatId: String = System.currentTimeMillis().toString()
        private set

    //  Chat History
    val chatHistory = mutableStateListOf<ChatHistoryModel>()

    // Initialize Gemini model with optimized configuration
    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
//            modelName = "gemini-2.5-flash",
            apiKey = Constants.API_KEY,
//            apiKey = key,
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE),

                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE)
            ),
            generationConfig = generationConfig {
                temperature = 0.9f // More creative responses
                topK = 40
            }
        )
    }

//    private val chat: Chat by lazy { generativeModel.startChat() }

    private var chat: Chat? = null
    fun initializeChat() {
        chat = generativeModel.startChat()
    }

    // Send message with rate limiting and retry logic
    fun sendMessage(question: String, imageBitmap: Bitmap? = null) {
        if (question.isBlank() && imageBitmap == null) {
            Log.d("ChatViewModel", "No input to send.")
            return
        }

        viewModelScope.launch {
            try {
                // Rate limiting check
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastRequestTime < minRequestInterval) {
                    val delayTime = minRequestInterval - (currentTime - lastRequestTime)
                    delay(delayTime + Random.nextLong(500)) // Add jitter
                }
                lastRequestTime = System.currentTimeMillis()

                _isLoading.value = true
                _apiError.value = null

                // Add user message with unique ID
                val userMessageId = UUID.randomUUID().toString()

                val userMsg = MessageModel(
                    id = userMessageId,
                    message = question.ifBlank { "[Image]" },
                    role = "user",
                    type = if (imageBitmap != null) "image" else "text",
                    bitmap = imageBitmap
                )
                messageList.add(userMsg)
                saveToFirestore(userMsg)

                // Add temporary loading message
                val loadingMessageId = UUID.randomUUID().toString()
                messageList.add(
                    MessageModel(
                        id = loadingMessageId,
                        message = "Analyzing...",
                        role = "model",
                        type = "text"
                    )
                )
                delay(100L)

                // Prepare content with error handling
                val inputContent = content {
                    if (question.isNotBlank()) {
                        text(question)
                    }
                    imageBitmap?.let { image(it) }
                }

                // Retry logic (3 attempts with backoff)
                var attempts = 0
                var responseText = "No response received."
                var lastError: Exception? = null

                while (attempts < 3) {
                    try {
                        val response = chat?.sendMessage(inputContent)
                            ?: throw IllegalStateException("Chat is not initialized")
                        responseText = response?.text ?: "No response received."
                        break
                    } catch (e: Exception) {
                        lastError = e
                        attempts++
                        if (attempts < 3) {
                            delay(1000L * attempts) // Exponential backoff
                        }
                    }
                }

                // Remove loading message
                 messageList.removeAll { it.id == loadingMessageId }

                if (lastError != null && attempts == 3) {
                    throw lastError
                }
                val modelMsg = MessageModel(
                    id = UUID.randomUUID().toString(),
                    message = responseText,
                    role = "model",
                    type = "text",
                    timestamp = System.currentTimeMillis()
//                    bitmap = imageBitmap
                )
                messageList.add(modelMsg)
                saveToFirestore(modelMsg)

                Log.i("ChatViewModel", "AI response: ${responseText.take(50)}...")

            } catch (e: Exception) {
                _apiError.value = when {

                    e.message?.contains("503") == true -> "Server busy. Please try again later."
                    e.message?.contains("429") == true -> "Too many requests. Please wait."
                    else -> "Error: ${e.message ?: "Unknown error"}"
                }

                // Remove loading message if still present
                messageList.removeAll { it.message == "Analyzing..." }

                Log.e("ChatViewModel", "API Error", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Clear conversation while maintaining rate limit awareness
    fun clearChat() {
        if (messageList.isNotEmpty()) {
            currentChatId = System.currentTimeMillis().toString() // New Chat session Id
            lastRequestTime = 0L // Reset rate limit on clear
            messageList.clear()
            initializeChat()
            _apiError.value = null
            Log.d("GeminiViewModel", "Chat is cleared")
        }
    }

    fun removeLastModelMessage() {
        val index = messageList.indexOfLast { it.role == "model" }
        if(index != -1) {
            messageList.removeAt(index)
        }
    }

    // This save each message in Firebase
    private fun saveToFirestore(message: MessageModel) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        Log.d("FIREBASE_UID", "Developer UID: $userId")
        val database = Firebase.firestore

        // Save timestamp to chats/<chatId> if it doesn't exist
        val chatRef = database.collection("GeminiChats")
            .document(userId)
            .collection("chats")
            .document(currentChatId)

        chatRef.set(mapOf("timestamp" to System.currentTimeMillis()), SetOptions.merge())

        chatRef.collection("messages")
            .add(message)
            .addOnFailureListener {
                Log.d("FireStore", "Failed to save message: ${it.message}")
            }
    }

    // Get list of all chat sessionId & timestamps (Display list in drawer
    fun fetchChatHistory() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = Firebase.firestore

        db.collection("GeminiChats")
            .document(userId)
            .collection("chats")
            .get()
            .addOnSuccessListener { result ->
                chatHistory.clear()
                for(doc in result) {
                    val chatId = doc.id
                    val timeStamp = chatId.toLongOrNull() ?: 0L
                    chatHistory.add(ChatHistoryModel(chatId, timeStamp))
                    Log.e("GeminiViewModel", "Message loaded successfully: ${timeStamp}")
                }
            }
            .addOnFailureListener {
                Log.e("GeminiViewModel", "Failed to fetch chat history: ${it.message}")
            }
    }

    // Get all messages from Firebase on chatId (Restore chat on screen when selected)
    fun loadChatById(chatId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = Firebase.firestore

        db.collection("GeminiChats")
            .document(userId)
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                messageList.clear()
                currentChatId = chatId
                for(doc in result) {
                    val msg = doc.toObject(MessageModel::class.java)
                    messageList.add(msg)
                    Log.e("GeminiViewModel", "Message loaded successfully: ${msg.message}")

                }
            }
            .addOnFailureListener {
                Log.e("GeminiViewModel", "Failed to load message: ${it.message}")
            }

    }

    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}