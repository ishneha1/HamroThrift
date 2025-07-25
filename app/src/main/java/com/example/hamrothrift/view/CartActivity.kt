package com.example.hamrothrift.view.buy

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import coil.compose.AsyncImage
import com.example.hamrothrift.R
import com.example.hamrothrift.model.CartItem
import com.example.hamrothrift.repository.CartRepositoryImpl
import com.example.hamrothrift.view.components.CommonTopAppBar
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.CartViewModel
import com.example.hamrothrift.viewmodel.CartViewModelFactory
import java.text.NumberFormat
import java.util.*

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = CartRepositoryImpl(this)
            val viewModel: CartViewModel = viewModel(
                factory = CartViewModelFactory(repository)
            )
            CartActivityBody(viewModel)
        }
    }
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
    val cartItemCount by viewModel.cartItemCount.collectAsState()

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
                    IconButton(onClick = { (context as ComponentActivity).finish() }) {
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
            // Cart Header
            Text(
                text = "My Cart",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(16.dp)
            )

            CartScreen(viewModel)
        }
    }
}

@Composable
fun CartScreen(viewModel: CartViewModel) {
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
                    val intent = Intent(context, SearchActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }

        else -> {
            Column(modifier = Modifier.fillMaxSize()) {
                // Cart items list
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

                // Total and checkout section
                CheckoutSection(
                    totalPrice = totalPrice,
                    onCheckout = {
                        Toast.makeText(context, "Checkout functionality coming soon!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyCartView(
    modifier: Modifier = Modifier,
    onContinueShopping: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Empty Cart",
                tint = White.copy(alpha = 0.7f),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Your cart is empty",
                style = TextStyle(
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onContinueShopping,
                colors = ButtonDefaults.buttonColors(containerColor = buttton),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Continue Shopping",
                    color = White
                )
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val itemTotal = cartItem.product.price * cartItem.quantity

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = card),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Product Image
                AsyncImage(
                    model = cartItem.product.imageUrl,
                    contentDescription = cartItem.product.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Product Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = cartItem.product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = text
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Rs.${cartItem.product.price}",
                        color = buttton,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Text(
                        text = cartItem.product.condition,
                        color = text.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }

                // Delete Button
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quantity Selector and Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuantitySelector(
                    currentQuantity = cartItem.quantity,
                    onQuantityChange = onQuantityChange,
                )

                Text(
                    text = "Rs.${itemTotal}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = text
                )
            }
        }
    }
}

@Composable
fun QuantitySelector(
    currentQuantity: Int,
    onQuantityChange: (Int) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = buttton.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(4.dp)
    ) {
        IconButton(
            onClick = {
                if (currentQuantity > 1) {
                    onQuantityChange(currentQuantity - 1)
                }
            },
            modifier = Modifier.size(32.dp)
        ) {
            Text(
                text = "-",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = buttton
            )
        }

        Text(
            text = currentQuantity.toString(),
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = text,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        IconButton(
            onClick = { onQuantityChange(currentQuantity + 1) },
            modifier = Modifier.size(32.dp)
        ) {
            Text(
                text = "+",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = buttton
            )
        }
    }
}

@Composable
fun CheckoutSection(
    totalPrice: Double,
    onCheckout: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = card),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = text
                )
                Text(
                    "Rs.${totalPrice}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = buttton
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttton),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Proceed to Checkout",
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                }
            }
        }
    }
}