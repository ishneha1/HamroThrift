package com.example.hamrothrift.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import com.example.hamrothrift.repository.CartRepositoryImpl
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.view.components.CartItemCard
import com.example.hamrothrift.view.components.CheckoutSection
import com.example.hamrothrift.view.components.EmptyCartView
import com.example.hamrothrift.viewmodel.CartViewModel
import com.example.hamrothrift.viewmodel.CartViewModelFactory

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CartScreen()
        }
    }
}

@Composable
fun CartScreen() {
    val context = LocalContext.current
    val repository = CartRepositoryImpl(context)
    val viewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(repository)
    )
    CartActivityBody(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartActivityBody(viewModel: CartViewModel) {
    val context = LocalContext.current
    val font = FontFamily(Font(R.font.handmade))
    val gradientColors = listOf(White, deepBlue, Color.Black)

    val cartItems by viewModel.cartItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()

    LaunchedEffect(Unit) {
        // Force reload cart items when screen appears
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
                navigationIcon = {
                    IconButton(onClick = {
                        val activity = context as? ComponentActivity
                        activity?.finish()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(innerPadding)
        ) {
            Text(
                text = "My Cart (${cartItems.size} items)",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = text
                ),
                modifier = Modifier.padding(16.dp)
            )

            CartContent(viewModel)
        }
    }
}

@Composable
fun CartContent(viewModel: CartViewModel) {
    val context = LocalContext.current
    val cartItems by viewModel.cartItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = buttton)
            }
        }

        cartItems.isEmpty() -> {
            EmptyCartView(
                modifier = Modifier.fillMaxSize(),
                onContinueShopping = {
                    val activity = context as? ComponentActivity
                    activity?.finish()
                }
            )
        }

        else -> {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cartItems, key = { it.id }) { item ->
                        CartItemCard(
                            cartItem = item,
                            onQuantityChange = { newQuantity ->
                                viewModel.updateQuantity(item.id, newQuantity)
                            },
                            onRemove = {
                                viewModel.removeItem(item.id)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CheckoutSection(
                    totalPrice = totalPrice,
                    onCheckout = {
                        if (cartItems.isNotEmpty()) {
                            // Navigate to CheckoutActivity
                            val intent = Intent(context, CheckoutActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "Cart is empty!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}