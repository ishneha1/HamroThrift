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
import androidx.compose.material.icons.filled.*
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
import com.example.hamrothrift.view.buy.CartActivity
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
            NotificationScreen(viewModel, mode)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(viewModel: NotificationViewModel, mode: String) {
    val context = LocalContext.current
    val activity = context as? Activity
    val gradientColors = listOf(White, deepBlue, Color.Black)
    var selectedTab by remember { mutableStateOf(2) }
    val font = FontFamily(Font(R.font.handmade))

    val notifications by viewModel.notifications.observeAsState(initial = emptyList<NotificationModel>())
    val isLoading by viewModel.loading.observeAsState(initial = false)
    val error by viewModel.error.observeAsState(initial = null)

    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
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
                        val intent = Intent(context, CartActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.ShoppingCart, "Cart", tint = Color.White)
                    }
                    IconButton(onClick = {
                        val intent = Intent(context, SearchActivity::class.java)
                        context.startActivity(intent)
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
                                val intent = Intent(context, NotificationActivity::class.java)
                                intent.putExtra("mode", mode)
                                context.startActivity(intent)
                                activity?.finish()
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
                                val intent = Intent(context, NotificationActivity::class.java)
                                intent.putExtra("mode", mode)
                                context.startActivity(intent)
                                activity?.finish()
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
                            text = "Notifications",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = text
                        )
                        TextButton(
                            onClick = { viewModel.clearAllNotifications() }
                        ) {
                            Text("Clear All", fontSize = 15.sp, color = text)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (notifications.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text("No notifications", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(notifications) { notification ->
                                NotificationCard(
                                    notification = notification,
                                    onDelete = { viewModel.deleteNotification(notification.notificationId) }
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
    onDelete: () -> Unit
) {
    val icon = when (notification.type) {
        "ORDER" -> Icons.Default.CheckCircle
        "MESSAGE" -> Icons.Default.Email
        "OFFER" -> Icons.Default.Star
        else -> Icons.Default.Notifications
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Teal)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = notification.title,
                tint = Color.Black,
                modifier = Modifier.size(35.dp)
            )

            Spacer(modifier = Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = Color.Black
                )
                Text(
                    text = notification.message,
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = formatTimestamp(notification.timestamp),
                    fontSize = 15.sp,
                    color = Color.Gray
                )
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

// Updated helper function to handle Long timestamp (Realtime Database)
private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}