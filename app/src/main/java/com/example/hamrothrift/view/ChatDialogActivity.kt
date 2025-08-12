package com.example.hamrothrift.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hamrothrift.repository.ChatRepoImpl
import com.example.hamrothrift.repository.NotificationRepoImpl
import com.example.hamrothrift.repository.ProductRepoImpl
import com.example.hamrothrift.view.components.ChatDialogScreen
import com.example.hamrothrift.view.theme.ui.theme.HamroThriftTheme
import com.example.hamrothrift.viewmodel.ChatDialogViewModel
import com.example.hamrothrift.viewmodel.ChatDialogViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class ChatDialogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check authentication
        if (FirebaseAuth.getInstance().currentUser == null) {
            finish()
            return
        }

        val productId = intent.getStringExtra("productId") ?: return
        val otherUserId = intent.getStringExtra("otherUserId") ?: return
        val message = intent.getStringExtra("message") ?: ""

        val viewModel = ChatDialogViewModelFactory(
            ChatRepoImpl(),
            NotificationRepoImpl(),
            ProductRepoImpl()
        ).create(ChatDialogViewModel::class.java)

        setContent {
            HamroThriftTheme {
                ChatDialogScreen(
                    productId = productId,
                    otherUserId = otherUserId,
                    initialMessage = message,
                    viewModel = viewModel,
                    onDismiss = { finish() }
                )
            }
        }
    }
}