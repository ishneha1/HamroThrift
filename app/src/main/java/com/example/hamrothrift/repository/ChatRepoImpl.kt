package com.example.hamrothrift.repository

import com.example.hamrothrift.model.ChatMessage
import com.example.hamrothrift.model.NotificationModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepoImpl : ChatRepo {
    private val firestore = FirebaseFirestore.getInstance()
    private val chatsCollection = firestore.collection("chats")
    private val notificationsCollection = firestore.collection("notifications")

    override suspend fun sendMessage(message: ChatMessage): Boolean {
        return try {
            val messageRef = chatsCollection.document()
            val messageWithId = message.copy(id = messageRef.id)
            messageRef.set(messageWithId).await()

            // Create notification for receiver
            val notification = NotificationModel(
                userId = message.receiverId,
                title = "New Message",
                message = "You have a new message about a product",
                type = "CHAT",
                timestamp = Timestamp.now(),
                relatedId = message.productId
            )
            notificationsCollection.add(notification).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getChatsForUser(userId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = chatsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ChatMessage::class.java)
                    }
                    trySend(messages)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun markMessageAsRead(messageId: String): Boolean {
        return try {
            chatsCollection.document(messageId)
                .update("isRead", true)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}