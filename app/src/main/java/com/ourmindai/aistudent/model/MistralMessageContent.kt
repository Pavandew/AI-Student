package com.ourmindai.aistudent.model

// User Message Content
data class MistralMessageContent(
    val type: String = "text",
    val text: String
)

// User Message Wrapper
data class Message(
    val role: String = "user",
    val content: List<MistralMessageContent>

)

// RequestBody
data class ChatRequest(
    val model: String,
    val messages: List<Message>
)

// Model Message Response
data class ResponseMessage(
    val role: String,
     val content: String  // this is plain String, not a list
)

// Each choice returned
data class ChatChoice (
    val message: ResponseMessage
)

data class ChatResponse(
    val choice: List<ChatChoice>
)