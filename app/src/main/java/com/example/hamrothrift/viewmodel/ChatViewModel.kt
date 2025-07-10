package com.example.hamrothrift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hamrothrift.model.ChatMessage
import com.example.hamrothrift.repository.ChatRepo
import com.example.hamrothrift.repository.NotificationRepo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepo: ChatRepo,
    private val notificationRepo: NotificationRepo
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun sendMessageToSeller(sellerId: String, productId: String, message: String, productName: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val chatMessage = ChatMessage(
                id = "",
                senderId = currentUserId,
                receiverId = sellerId,
                productId = productId,
                message = message,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )

            val success = chatRepo.sendMessage(chatMessage)
            _isLoading.value = false
        }
    }

    fun getChatsForUser() {
        viewModelScope.launch {
            chatRepo.getChatsForUser(currentUserId).collect { chatList ->
                _messages.value = chatList
            }
        }
    }
}