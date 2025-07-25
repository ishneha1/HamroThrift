package com.example.hamrothrift.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.hamrothrift.repository.ChatRepoImpl
import com.example.hamrothrift.repository.NotificationRepoImpl
import com.example.hamrothrift.view.components.ChatScreen
import com.example.hamrothrift.viewmodel.ChatViewModel
import com.example.hamrothrift.viewmodel.ChatViewModelFactory

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val productId = intent.getStringExtra("productId") ?: ""
        val otherUserId = intent.getStringExtra("otherUserId") ?: ""
        setContent {
            val chatViewModel = ChatViewModelFactory(ChatRepoImpl(), NotificationRepoImpl())
                .create(ChatViewModel::class.java)
            ChatScreen(
                productId = productId,
                otherUserId = otherUserId,
                chatViewModel = chatViewModel
            )
        }
    }
}