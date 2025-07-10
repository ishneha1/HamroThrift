package com.example.hamrothrift.repository

import com.example.hamrothrift.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepo {
    suspend fun sendMessage(message: ChatMessage): Boolean
    fun getChatsForUser(userId: String): Flow<List<ChatMessage>>
    suspend fun markMessageAsRead(messageId: String): Boolean
}