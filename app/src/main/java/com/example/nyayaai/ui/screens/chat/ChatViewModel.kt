package com.example.nyayaai.ui.screens.chat

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nyayaai.data.PreferenceManager
import com.example.nyayaai.remote.ChatRequest
import com.example.nyayaai.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class Message(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val preferenceManager = PreferenceManager(application)
    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            val savedMessages = preferenceManager.chatMessages.first()
            if (savedMessages.isEmpty()) {
                val initialMessage = Message("Hello! I am NyayaAI. How can I assist you with legal guidance today?", false)
                _messages.add(initialMessage)
                saveToPersistence()
            } else {
                _messages.addAll(savedMessages)
            }
        }
    }

    private suspend fun saveToPersistence() {
        preferenceManager.saveMessages(_messages.toList())
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = Message(text, true)
        _messages.add(userMessage)
        _isLoading.value = true

        viewModelScope.launch {
            saveToPersistence()
            try {
                val response = RetrofitClient.chatApi.sendMessage(ChatRequest(text))
                val aiMessage = Message(response.response, false)
                _messages.add(aiMessage)
                saveToPersistence()
            } catch (e: Exception) {
                android.util.Log.e("ChatViewModel", "Error sending message: ${e.message}", e)
                val errorMessage = Message("I'm sorry, I'm having trouble connecting to my legal database. Please check your connection or server address.", false)
                _messages.add(errorMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }
}