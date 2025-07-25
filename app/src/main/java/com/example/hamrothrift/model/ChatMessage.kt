package com.example.hamrothrift.model

data class ChatMessage(
    val id: String = "",
    val senderId: String?= null,
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false,
    val productId: String = ""
)