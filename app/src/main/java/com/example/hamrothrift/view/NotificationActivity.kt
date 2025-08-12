package com.example.hamrothrift.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamrothrift.R
import com.example.hamrothrift.model.NotificationModel
import com.example.hamrothrift.repository.NotificationRepoImpl
import com.example.hamrothrift.view.buy.DashboardActivityBuy
import com.example.hamrothrift.view.buy.SaleActivity
import com.example.hamrothrift.view.buy.SearchActivity
import com.example.hamrothrift.view.sell.DashboardSellActivity
import com.example.hamrothrift.view.sell.UploadActivity
import com.example.hamrothrift.view.components.CommonBottomBar
import com.example.hamrothrift.view.components.CommonBottomBarSell
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.NotificationViewModel
import com.example.hamrothrift.viewmodel.NotificationViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val notificationRepo = NotificationRepoImpl()
            val viewModel: NotificationViewModel = viewModel(
                factory = NotificationViewModelFactory(notificationRepo)
            )
            val mode = intent.getStringExtra("mode") ?: "buy"
            NotificationListScreen()
        }
    }
}

@Composable
fun NotificationListScreen() {
    val notificationRepo = NotificationRepoImpl()
    val viewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(notificationRepo)
    )
    val context = LocalContext.current
    val mode = if (context is Activity) {
        context.intent?.getStringExtra("mode") ?: "buy"
    } else {
        "buy"
    }
    NotificationScreen(viewModel, mode)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(viewModel: NotificationViewModel, mode: String) {
    val context = LocalContext.current
    val activity = context as? Activity
    val gradientColors = listOf(White, deepBlue, Color.Black)
    var selectedTab by remember { mutableStateOf(2) }
    val font = FontFamily(Font(R.font.handmade))
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    val notifications by viewModel.notifications.observeAsState(initial = emptyList())
    val isLoading by viewModel.loading.observeAsState(initial = false)
    val error by viewModel.error.observeAsState(initial = null)

    // Auto-refresh notifications every 30 seconds for real-time updates
    LaunchedEffect(Unit) {
        viewModel.loadNotifications()

        // Real-time refresh every 30 seconds
        while (true) {
            kotlinx.coroutines.delay(30000) // 30 seconds
            viewModel.loadNotifications()
        }
    }

    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            viewModel.clearError()
        }
    }

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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appBar),
                actions = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, CartActivity::class.java))
                    }) {
                        Icon(Icons.Default.ShoppingCart, "Cart", tint = Color.White)
                    }
                    IconButton(onClick = {
                        context.startActivity(Intent(context, SearchActivity::class.java))
                    }) {
                        Icon(Icons.Default.Search, "Search", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            if (mode == "sell") {
                CommonBottomBarSell(
                    selectedTab = selectedTab,
                    onTabSelected = { index ->
                        selectedTab = index
                        when (index) {
                            0 -> {
                                context.startActivity(Intent(context, DashboardSellActivity::class.java))
                                activity?.finish()
                            }
                            1 -> {
                                context.startActivity(Intent(context, UploadActivity::class.java))
                                activity?.finish()
                            }
                            2 -> {
                                // Stay on notifications - reload
                                viewModel.loadNotifications()
                            }
                            3 -> {
                                val intent = Intent(context, ProfileActivity::class.java)
                                intent.putExtra("mode", mode)
                                context.startActivity(intent)
                                activity?.finish()
                            }
                        }
                    }
                )
            } else {
                CommonBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { index ->
                        selectedTab = index
                        when (index) {
                            0 -> {
                                context.startActivity(Intent(context, DashboardActivityBuy::class.java))
                                activity?.finish()
                            }
                            1 -> {
                                context.startActivity(Intent(context, SaleActivity::class.java))
                                activity?.finish()
                            }
                            2 -> {
                                // Stay on notifications - reload
                                viewModel.loadNotifications()
                            }
                            3 -> {
                                val intent = Intent(context, ProfileActivity::class.java)
                                intent.putExtra("mode", mode)
                                context.startActivity(intent)
                                activity?.finish()
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 15.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Messages & Notifications",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = text
                        )
                        Row {
                            TextButton(onClick = { viewModel.loadNotifications() }) {
                                Text("Refresh", fontSize = 15.sp, color = text)
                            }
                            TextButton(onClick = { viewModel.clearAllNotifications() }) {
                                Text("Clear All", fontSize = 15.sp, color = text)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filter notifications for current user (both MESSAGE and other types)
                    val filteredNotifications = notifications.filter { notification ->
                        notification.userId == currentUserId
                    }

                    // Show unread count
                    val unreadCount = filteredNotifications.count { !it.isRead }
                    if (unreadCount > 0) {
                        Text(
                            text = "$unreadCount unread notifications",
                            fontSize = 14.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (filteredNotifications.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No notifications yet", color = Color.Gray, fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Messages from buyers and sellers will appear here",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredNotifications) { notification ->
                                NotificationCard(
                                    notification = notification,
                                    onDelete = { viewModel.deleteNotification(notification.notificationId) },
                                    onMarkAsRead = {
                                        if (!notification.isRead) {
                                            viewModel.markAsRead(notification.notificationId)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationModel,
    onDelete: () -> Unit,
    onMarkAsRead: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Teal.copy(alpha = 0.7f) else Teal
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Different icons based on notification type
            Icon(
                imageVector = when (notification.type) {
                    "MESSAGE" -> Icons.Default.Email
                    "ORDER" -> Icons.Default.ShoppingCart
                    else -> Icons.Default.Email
                },
                contentDescription = notification.title,
                tint = if (notification.isRead) Color.Gray else Color.Black,
                modifier = Modifier.size(35.dp)
            )

            Spacer(modifier = Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                    fontSize = 19.sp,
                    color = if (notification.isRead) Color.Gray else Color.Black
                )
                Text(
                    text = notification.message,
                    fontSize = 16.sp,
                    color = if (notification.isRead) Color.Gray else Color.DarkGray,
                    maxLines = 2
                )
                Text(
                    text = formatTimestamp(notification.timestamp),
                    fontSize = 15.sp,
                    color = Color.Gray
                )

                // Show sender info if available
                if (notification.senderInfo.isNotEmpty()) {
                    Text(
                        text = "From: ${notification.senderInfo}",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            Column {
                if (!notification.isRead) {
                    TextButton(onClick = onMarkAsRead) {
                        Text("Read", color = Color.Blue, fontSize = 12.sp)
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}