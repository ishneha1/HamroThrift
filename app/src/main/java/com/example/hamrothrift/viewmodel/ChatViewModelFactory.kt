package com.example.hamrothrift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hamrothrift.repository.ChatRepo
import com.example.hamrothrift.repository.NotificationRepo

class ChatViewModelFactory(
    private val chatRepo: ChatRepo,
    private val notificationRepo: NotificationRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(chatRepo, notificationRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}