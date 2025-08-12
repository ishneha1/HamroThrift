package com.example.hamrothrift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hamrothrift.repository.ChatRepo
import com.example.hamrothrift.repository.NotificationRepo
import com.example.hamrothrift.repository.ProductRepo

class ChatDialogViewModelFactory(
    private val chatRepo: ChatRepo,
    private val notificationRepo: NotificationRepo,
    private val productRepo: ProductRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatDialogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatDialogViewModel(chatRepo, notificationRepo, productRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}