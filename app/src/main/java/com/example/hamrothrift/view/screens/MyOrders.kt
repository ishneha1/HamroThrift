package com.example.hamrothrift.view.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.hamrothrift.repository.OrderRepositoryImpl
import com.example.hamrothrift.view.ProfileActivity
import com.example.hamrothrift.view.buy.DashboardActivityBuy
import com.example.hamrothrift.view.buy.NotificationActivity
import com.example.hamrothrift.view.buy.SaleActivity
import com.example.hamrothrift.view.components.CommonBottomBar
import com.example.hamrothrift.view.components.OrderCard
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.OrderViewModel
import com.example.hamrothrift.viewmodel.OrderViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class MyOrdersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val orderRepository = OrderRepositoryImpl()
            val viewModel: OrderViewModel = viewModel(
                factory = OrderViewModelFactory(orderRepository)
            )
            MyOrdersActivityBody(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyOrdersActivityBody(viewModel: OrderViewModel) {
    var selectedTab by remember { mutableStateOf(-1) }
    val context = LocalContext.current
    val gradientColors = listOf(White, deepBlue, Color.Black)
    val font = FontFamily(Font(R.font.handmade))

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
                navigationIcon = {
                    IconButton(onClick = { (context as ComponentActivity).finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }
                }
            )
        },
        bottomBar = {
            CommonBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { index ->
                    when (index) {
                        0 -> context.startActivity(Intent(context, DashboardActivityBuy::class.java))
                        1 -> context.startActivity(Intent(context, SaleActivity::class.java))
                        2 -> context.startActivity(Intent(context, NotificationActivity::class.java))
                        3 -> context.startActivity(Intent(context, ProfileActivity::class.java))
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(bg)
        ) {
            MyOrdersScreen(viewModel = viewModel)
        }
    }
}

@Composable
fun MyOrdersScreen(viewModel: OrderViewModel) {
    val user = FirebaseAuth.getInstance().currentUser
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val font = FontFamily(Font(R.font.handmade))

    LaunchedEffect(user) {
        user?.uid?.let { userId ->
            viewModel.getUserOrders(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "My Orders",
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            color = text,
            fontFamily = font,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = buttton)
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: $error",
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            }

            orders.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "No Orders",
                            modifier = Modifier.size(64.dp),
                            tint = text.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No orders found",
                            fontSize = 18.sp,
                            color = text.copy(alpha = 0.7f),
                            fontFamily = font
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders) { order ->
                        OrderCard(
                            order = order,
                            showBuyerInfo = false // Don't show buyer info in My Orders
                        )
                    }
                }
            }
        }
    }
}

