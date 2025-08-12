package com.example.hamrothrift.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hamrothrift.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChatScreen(
    productId: String,
    otherUserId: String,
    chatViewModel: ChatViewModel
) {
    val messages by chatViewModel.messages.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Load messages for this chat (filter by productId and otherUserId)
    LaunchedEffect(productId, otherUserId) {
        chatViewModel.getChatsForUser() // Loads all, filter below
    }

    val filteredMessages = messages.filter {
        (it.productId == productId) &&
                ((it.senderId == currentUserId && it.receiverId == otherUserId) ||
                        (it.senderId == otherUserId && it.receiverId == currentUserId))
    }.sortedBy { it.timestamp }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        Text(
            text = "Chat for Product",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        Divider()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = false
        ) {
            items(filteredMessages) { msg ->
                val isMe = msg.senderId == currentUserId
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        color = if (isMe) Color(0xFFD1E7DD) else Color(0xFFF8D7DA),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = msg.message,
                            modifier = Modifier.padding(10.dp),
                            color = Color.Black
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") }
            )
            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        // Send message to the other user for this product
                        chatViewModel.sendMessageToSeller(
                            sellerId = otherUserId,
                            productId = productId,
                            message = messageText,
                            productName = "" // Optional, or fetch if needed
                        )
                        messageText = ""
                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}