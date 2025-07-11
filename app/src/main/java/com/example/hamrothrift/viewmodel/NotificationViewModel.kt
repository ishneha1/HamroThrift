package com.example.hamrothrift.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hamrothrift.model.NotificationModel
import com.example.hamrothrift.repository.NotificationRepo
import com.google.firebase.auth.FirebaseAuth

class NotificationViewModel(
    private val notificationRepo: NotificationRepo
) : ViewModel() {
    private val _notifications = MutableLiveData<List<NotificationModel>>()
    val notifications: LiveData<List<NotificationModel>> = _notifications

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    fun loadNotifications() {
        _loading.value = true
        currentUserId?.let { userId ->
            notificationRepo.getNotificationsByUserId(userId) { success, message, notifications ->
                _loading.value = false
                if (success) {
                    _notifications.value = notifications.sortedByDescending { it.timestamp }
                } else {
                    _error.value = message
                }
            }
        }
    }

    fun clearAllNotifications() {
        _loading.value = true
        currentUserId?.let { userId ->
            notificationRepo.clearAllNotifications(userId) { success, message ->
                _loading.value = false
                if (!success) {
                    _error.value = message
                }
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        _loading.value = true
        notificationRepo.deleteNotification(notificationId) { success, message ->
            _loading.value = false
            if (!success) {
                _error.value = message
            }
        }
    }

    fun markAsRead(notificationId: String) {
        notificationRepo.updateNotificationStatus(notificationId, true) { success, message ->
            if (!success) {
                _error.value = message
            }
        }
    }

    fun addNotification(title: String, message: String, type: String) {
        currentUserId?.let { userId ->
            val notification = NotificationModel(
                title = title,
                message = message,
                timestamp = com.google.firebase.Timestamp.now(),
                userId = userId,
                type = type
            )

            notificationRepo.addNotification(notification) { success, message ->
                if (!success) {
                    _error.value = message
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}