package com.ourmindai.aistudent.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ourmindai.aistudent.Constants
import com.ourmindai.aistudent.Network.RetrofitInstance
import com.ourmindai.aistudent.model.ChatRequest
import com.ourmindai.aistudent.model.Message
import com.ourmindai.aistudent.model.MistralMessageContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException

class MistralChatViewModel: ViewModel() {
    val MISTRAL_MODEL = "mistralai/mistral-small-3.2-24b-instruct:free"
    val apikey = Constants.MISTRAL_API_KEY

    private val _response = MutableStateFlow("Mistral is ready!")
    val response = _response.asStateFlow()

    fun sendMessageToMistral(prompt: String, apikey: String) {
        viewModelScope.launch {
            "Mistral is thinking...".also { _response.value = it }

            val request = ChatRequest(
                model = MISTRAL_MODEL,
                messages = listOf(
                    Message(
                        role = "user",
                        content = listOf(MistralMessageContent(text = prompt))
                    )
                )
            )
            try {
                val result = RetrofitInstance.api.sendChatMessage(request, "Bearer $apikey").execute()

                if(result.isSuccessful) {
                    val body = result.body()
                    val outputText = body?.choice?.firstOrNull()?.message?.content ?: "No response"
                    _response.value = outputText
                    Log.d("MistralChatViewModel", "Response is: --> $outputText")
                } else {
                    _response.value = "Error ${result.code()} ${result.message()}"
                    Log.d("MistralChatViewModel", "HTTP Error: ${result.code()} ${result.message()}")
                }
            } catch (e: IOException) {

            }
        }
    }



}