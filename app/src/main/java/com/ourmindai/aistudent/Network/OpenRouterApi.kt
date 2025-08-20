package com.ourmindai.aistudent.Network

import com.ourmindai.aistudent.model.ChatRequest
import com.ourmindai.aistudent.model.ChatResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenRouterApi {

    @POST("chat/completions")
    fun sendChatMessage(
        @Body request: ChatRequest,
        @Header("Authorization") token: String
    ): Call<ChatResponse>
}