package com.example.hamrothrift.repository

import com.example.hamrothrift.model.NotificationModel

interface NotificationRepo {
    fun addNotification(
        notification: NotificationModel,
        callback: (Boolean, String) -> Unit
    )

    fun getNotificationsByUserId(
        userId: String,
        callback: (Boolean, String, List<NotificationModel>) -> Unit
    )

    fun deleteNotification(
        notificationId: String,
        callback: (Boolean, String) -> Unit
    )

    fun clearAllNotifications(
        userId: String,
        callback: (Boolean, String) -> Unit
    )

    fun updateNotificationStatus(
        notificationId: String,
        isRead: Boolean,
        callback: (Boolean, String) -> Unit
    )
}