package com.example.hamrothrift.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamrothrift.model.CartItem
import com.example.hamrothrift.model.PaymentResult
import com.example.hamrothrift.repository.CartRepositoryImpl
import com.example.hamrothrift.repository.PaymentRepositoryImpl
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.CartViewModel
import com.example.hamrothrift.viewmodel.CartViewModelFactory
import com.example.hamrothrift.viewmodel.PaymentViewModel
import com.example.hamrothrift.viewmodel.PaymentViewModelFactory

class CheckoutActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CheckoutScreen { success ->
                if (success) {
                    val intent = Intent()
                    intent.putExtra("payment_success", true)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
fun CheckoutScreen(
    onPaymentResult: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val cartRepository = CartRepositoryImpl(context)
    val paymentRepository = PaymentRepositoryImpl(context)

    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(cartRepository)
    )

    val paymentViewModel: PaymentViewModel = viewModel(
        factory = PaymentViewModelFactory(paymentRepository)
    )

    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()
    val paymentState by paymentViewModel.paymentState.collectAsState()

    // Handle payment state changes
    LaunchedEffect(paymentState) {
        when (val currentState = paymentState) {
            is PaymentResult.Success -> {
                cartViewModel.clearCart()
                onPaymentResult(true)
            }
            is PaymentResult.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
                paymentViewModel.clearPaymentState()
            }
            else -> { /* Handle loading or null state */ }
        }
    }

    CheckoutContent(
        cartItems = cartItems,
        totalPrice = totalPrice,
        paymentState = paymentState,
        onPayment = { method ->
            paymentViewModel.processPayment(cartItems, totalPrice, method)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutContent(
    cartItems: List<CartItem>,
    totalPrice: Double,
    paymentState: PaymentResult?,
    onPayment: (String) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", color = White) },
                navigationIcon = {
                    IconButton(onClick = {
                        val activity = context as? ComponentActivity
                        activity?.finish()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appBar)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Order Summary",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = text,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cartItems) { item ->
                    CheckoutItemCard(item)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PaymentSummaryCard(
                totalPrice = totalPrice,
                paymentState = paymentState,
                onPayment = onPayment
            )
        }
    }
}

@Composable
fun CheckoutItemCard(cartItem: CartItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.product?.name ?: "Unknown Product",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = text
                )
                Text(
                    text = "Qty: ${cartItem.quantity}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = "Rs. ${String.format("%.2f", (cartItem.product?.price ?: 0.0) * cartItem.quantity)}",
                fontWeight = FontWeight.Bold,
                color = buttton
            )
        }
    }
}

@Composable
fun PaymentSummaryCard(
    totalPrice: Double,
    paymentState: PaymentResult?,
    onPayment: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal:", fontSize = 16.sp, color = text)
                Text("Rs. ${String.format("%.2f", totalPrice)}", fontSize = 16.sp, color = text)
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Total:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = text
                )
                Text(
                    "Rs. ${String.format("%.2f", totalPrice)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = buttton
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (paymentState) {
                is PaymentResult.Loading -> {
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Processing...", color = White)
                    }
                }
                else -> {
                    Button(
                        onClick = { onPayment("Mock Payment") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = buttton),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Complete Payment",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                }
            }
        }
    }
}