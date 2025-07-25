package com.example.hamrothrift.model

import com.google.firebase.Timestamp

data class NotificationModel(
    var notificationId: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false,
    val type: String = "", // "BUYER_MESSAGE", "SELLER_REPLY", "ORDER", etc.
    val relatedId: String = "", // productId, orderId, etc.
    val senderInfo: String = "" // Additional sender information
)