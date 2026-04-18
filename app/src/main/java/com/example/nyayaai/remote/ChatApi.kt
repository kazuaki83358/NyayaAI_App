package com.example.nyayaai.remote

import retrofit2.http.Body
import retrofit2.http.POST

data class ChatRequest(val message: String)
data class ChatResponse(val response: String)

interface ChatApi {
    @POST("chat")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
}