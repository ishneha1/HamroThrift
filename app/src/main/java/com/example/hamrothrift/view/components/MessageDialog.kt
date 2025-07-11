package com.example.hamrothrift.view.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamrothrift.R
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.repository.ChatRepoImpl
import com.example.hamrothrift.repository.NotificationRepoImpl
import com.example.hamrothrift.view.ProfileActivity
import com.example.hamrothrift.view.buy.DashboardActivityBuy
import com.example.hamrothrift.view.buy.NotificationActivity
import com.example.hamrothrift.view.buy.SaleActivity
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.ChatViewModel
import com.example.hamrothrift.viewmodel.ChatViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDialog(
    product: ProductModel,
    onDismiss: () -> Unit,
    chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(ChatRepoImpl(), NotificationRepoImpl())
    )
) {
    var messageText by remember { mutableStateOf("") }
    val isLoading by chatViewModel.isLoading.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val gradientColors = listOf(White, deepBlue, Black)
    val font = FontFamily(Font(R.font.handmade))

    Dialog(onDismissRequest = onDismiss) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "HamroThrift",
                            style = TextStyle(
                                brush = Brush.linearGradient(colors = gradientColors),
                                fontSize = 25.sp,
                                fontFamily = font,
                                fontStyle = FontStyle.Italic
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = buttton)
                )
            },
            bottomBar = {
                CommonBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { index: Int ->
                        selectedTab = index
                        onDismiss()
                        when (index) {
                            0 -> context.startActivity(Intent(context, DashboardActivityBuy::class.java))
                            1 -> context.startActivity(Intent(context, SaleActivity::class.java))
                            2 -> context.startActivity(Intent(context, NotificationActivity::class.java))
                            3 -> context.startActivity(Intent(context, ProfileActivity::class.java))
                        }
                    }
                )
            },
            containerColor = bg
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bg)
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Message Seller",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = text
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(product.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = text)
                        Text("Rs. ${product.price}", fontSize = 14.sp, color = buttton)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    label = { Text("Type your message...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            chatViewModel.sendMessageToSeller(
                                sellerId = product.sellerId,
                                productId = product.id,
                                message = messageText,
                                productName = product.name
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = buttton),
                    enabled = messageText.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = White)
                    } else {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Message", color = White)
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = text)
                }
            }
        }
    }
}