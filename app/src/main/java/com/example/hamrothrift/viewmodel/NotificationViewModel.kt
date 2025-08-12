package com.example.hamrothrift.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hamrothrift.model.NotificationModel
import com.example.hamrothrift.repository.NotificationRepo
import com.google.firebase.auth.FirebaseAuth

class NotificationViewModel(private val repository: NotificationRepo) : ViewModel() {

    private val _notifications = MutableLiveData<List<NotificationModel>>()
    val notifications: LiveData<List<NotificationModel>> = _notifications

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    fun loadNotifications() {
        currentUserId?.let { userId ->
            _loading.value = true
            repository.getNotificationsByUserId(userId) { success, message, notificationList ->
                _loading.value = false
                if (success) {
                    _notifications.value = notificationList.sortedByDescending { it.timestamp }
                } else {
                    _error.value = message
                }
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        repository.deleteNotification(notificationId) { success, message ->
            if (success) {
                loadNotifications() // Reload after deletion
            } else {
                _error.value = message
            }
        }
    }

    fun clearAllNotifications() {
        currentUserId?.let { userId ->
            repository.clearAllNotifications(userId) { success, message ->
                if (success) {
                    _notifications.value = emptyList()
                } else {
                    _error.value = message
                }
            }
        }
    }

    fun markAsRead(notificationId: String) {
        repository.updateNotificationStatus(notificationId, true) { success, message ->
            if (success) {
                loadNotifications() // Reload to update UI
            } else {
                _error.value = message
            }
        }
    }

    // Function to send message notification to buyer/seller
    fun sendMessageNotification(
        receiverId: String,
        senderName: String,
        messageText: String,
        productId: String? = null
    ) {
        val notification = NotificationModel(
            userId = receiverId,
            title = "New Message from $senderName",
            message = messageText,
            timestamp = System.currentTimeMillis(),
            isRead = false,
            senderId = currentUserId ?: "",
            productId = productId,
            type = "MESSAGE",
            relatedId = productId ?: "",
            senderInfo = senderName
        )

        repository.addNotification(notification) { success, message ->
            if (!success) {
                _error.value = message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}