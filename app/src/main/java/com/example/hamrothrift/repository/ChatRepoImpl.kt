package com.example.hamrothrift.repository

import com.example.hamrothrift.model.ChatMessage
import com.example.hamrothrift.model.NotificationModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepoImpl : ChatRepo {
    private val database = FirebaseDatabase.getInstance()
    private val chatsRef = database.reference.child("chats")
    private val notificationsRef = database.reference.child("notifications")

    override suspend fun sendMessage(message: ChatMessage): Boolean {
        return try {
            val messageRef = chatsRef.push()
            val messageWithId = message.copy(id = messageRef.key ?: "")
            messageRef.setValue(messageWithId).await()

            // Create notification for receiver
            val notificationRef = notificationsRef.push()
            val notification = NotificationModel(
                notificationId = notificationRef.key ?: "",
                userId = message.receiverId,
                title = "New Message",
                message = "You have a new message about a product",
                type = "CHAT",
                timestamp = System.currentTimeMillis(),
                relatedId = message.productId
            )
            notificationRef.setValue(notification).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getChatsForUser(userId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = chatsRef.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<ChatMessage>()
                for (childSnapshot in snapshot.children) {
                    val message = childSnapshot.getValue(ChatMessage::class.java)
                    message?.let {
                        // Filter messages for the user (either sender or receiver)
                        if (it.senderId == userId || it.receiverId == userId) {
                            messages.add(0, it) // Add to beginning for DESC order
                        }
                    }
                }
                trySend(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { chatsRef.removeEventListener(listener) }
    }

    override suspend fun markMessageAsRead(messageId: String): Boolean {
        return try {
            chatsRef.child(messageId).child("isRead").setValue(true).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}