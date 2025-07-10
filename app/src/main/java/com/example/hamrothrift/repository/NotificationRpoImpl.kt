package com.example.hamrothrift.repository

import com.example.hamrothrift.model.NotificationModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationRepoImpl : NotificationRepo {
    private val database = FirebaseDatabase.getInstance()
    private val notificationsRef = database.getReference("notifications")

    override fun addNotification(
        notification: NotificationModel,
        callback: (Boolean, String) -> Unit
    ) {
        var notificationId = notificationsRef.push().key ?: return
        notification.notificationId = notificationId

        notificationsRef.child(notificationId).setValue(notification)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Notification added successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to add notification")
                }
            }
    }

    override fun getNotificationsByUserId(
        userId: String,
        callback: (Boolean, String, List<NotificationModel>) -> Unit
    ) {
        notificationsRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notifications = mutableListOf<NotificationModel>()
                    for (notificationSnap in snapshot.children) {
                        val notification = notificationSnap.getValue(NotificationModel::class.java)
                        notification?.let { notifications.add(it) }
                    }
                    callback(true, "Notifications fetched successfully", notifications)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun deleteNotification(
        notificationId: String,
        callback: (Boolean, String) -> Unit
    ) {
        notificationsRef.child(notificationId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Notification deleted successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to delete notification")
                }
            }
    }

    override fun clearAllNotifications(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        notificationsRef.orderByChild("userId").equalTo(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val updates = HashMap<String, Any?>()
                snapshot.children.forEach {
                    updates[it.key!!] = null
                }
                notificationsRef.updateChildren(updates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            callback(true, "All notifications cleared")
                        } else {
                            callback(false, task.exception?.message ?: "Failed to clear notifications")
                        }
                    }
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to clear notifications")
            }
    }

    override fun updateNotificationStatus(
        notificationId: String,
        isRead: Boolean,
        callback: (Boolean, String) -> Unit
    ) {
        val updates = hashMapOf<String, Any>(
            "isRead" to isRead
        )

        notificationsRef.child(notificationId).updateChildren(updates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Notification status updated")
                } else {
                    callback(false, task.exception?.message ?: "Failed to update notification status")
                }
            }
    }
}