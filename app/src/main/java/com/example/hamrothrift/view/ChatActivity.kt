package com.example.hamrothrift.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.hamrothrift.repository.ChatRepoImpl
import com.example.hamrothrift.repository.NotificationRepoImpl
import com.example.hamrothrift.view.components.ChatScreen
import com.example.hamrothrift.viewmodel.ChatViewModel
import com.example.hamrothrift.viewmodel.ChatViewModelFactory
import com.example.hamrothrift.viewmodel.NotificationViewModel
import com.google.firebase.auth.FirebaseAuth

// Example: In your ChatActivity or MessageActivity
class ChatActivity : ComponentActivity() {
    private lateinit var notificationViewModel: NotificationViewModel

    private fun sendMessage(messageText: String, receiverId: String, receiverName: String) {
        val productId = intent.getStringExtra("PRODUCT_ID") ?: ""

        notificationViewModel.sendMessageNotification(
            receiverId = receiverId,
            senderName = getCurrentUserName(), // Get current user's name
            messageText = messageText,
            productId = productId // If related to a specific product
        )
    }

    private fun getCurrentUserName(): String {
        // Return current user's display name
        return FirebaseAuth.getInstance().currentUser?.displayName ?: "Unknown User"
    }
}