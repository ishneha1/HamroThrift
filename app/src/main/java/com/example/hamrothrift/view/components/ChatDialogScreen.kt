package com.example.hamrothrift.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.viewmodel.ChatDialogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDialogScreen(
    productId: String,
    otherUserId: String,
    initialMessage: String,
    viewModel: ChatDialogViewModel,
    onDismiss: () -> Unit
) {
    var replyText by remember { mutableStateOf(initialMessage) }
    val messages by viewModel.messages.collectAsState()
    val product by viewModel.product.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProduct(productId)
        viewModel.loadMessages(productId, otherUserId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Product Overview Card
            product?.let { prod ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Product Image
                        Card(
                            modifier = Modifier.size(80.dp),
                            shape = MaterialTheme.shapes.small
                        ) {
                            AsyncImage(
                                model = prod.imageUrl?.firstOrNull(),
                                contentDescription = "Product Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Product Details
                        Column {
                            Text(
                                text = prod.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rs. ${prod.price}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = prod.condition,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Messages List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                reverseLayout = true
            ) {
                items(messages.reversed()) { message ->
                    MessageBubble(
                        message = message.message,
                        isFromCurrentUser = message.senderId == viewModel.getCurrentUserId(),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            // Input Field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (replyText.isNotBlank()) {
                            viewModel.sendReply(otherUserId, productId, replyText)
                            replyText = ""
                        }
                    }
                ) {
                    Text("Send")
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: String,
    isFromCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isFromCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(10.dp),
                color = Color.White
            )
        }
    }
}