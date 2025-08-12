package com.example.hamrothrift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hamrothrift.model.ChatMessage
import com.example.hamrothrift.model.NotificationModel
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.repository.ChatRepo
import com.example.hamrothrift.repository.NotificationRepo
import com.example.hamrothrift.repository.ProductRepo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatDialogViewModel(
    private val chatRepo: ChatRepo,
    private val notificationRepo: NotificationRepo,
    private val productRepo: ProductRepo
) : ViewModel() {
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _product = MutableStateFlow<ProductModel?>(null)
    val product: StateFlow<ProductModel?> = _product

    // Add this method
    fun getCurrentUserId(): String = currentUserId

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            try {
                val result = productRepo.getProductById(productId)
                result?.let {
                    _product.value = it
                }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun loadMessages(productId: String, otherUserId: String) {
        viewModelScope.launch {
            chatRepo.getChatsForUser(currentUserId).collect { chatList ->
                _messages.value = chatList.filter {
                    it.productId == productId &&
                            ((it.senderId == currentUserId && it.receiverId == otherUserId) ||
                                    (it.senderId == otherUserId && it.receiverId == currentUserId))
                }.sortedBy { it.timestamp }
            }
        }
    }

    fun sendReply(receiverId: String, productId: String, message: String) {
        viewModelScope.launch {
            val chatMessage = ChatMessage(
                senderId = currentUserId,
                receiverId = receiverId,
                productId = productId,
                message = message,
                timestamp = System.currentTimeMillis()
            )

            val success = chatRepo.sendMessage(chatMessage)
            if (success) {
                notificationRepo.addNotification(
                    NotificationModel(
                        userId = receiverId,
                        title = "New Reply",
                        message = message,
                        timestamp = System.currentTimeMillis(),
                        type = "MESSAGE",
                        senderId = currentUserId,
                        productId = productId,
                        relatedId = productId
                    )
                ) { _, _ -> }
            }
        }
    }
}