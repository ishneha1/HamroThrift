package com.example.hamrothrift.view.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hamrothrift.R
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.repository.CartRepositoryImpl
import com.example.hamrothrift.view.buy.CartActivity
import com.example.hamrothrift.view.buy.SearchActivity
import com.example.hamrothrift.view.theme.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopAppBar() {
    val font = FontFamily(Font(R.font.handmade))
    val context = LocalContext.current

    TopAppBar(
        title = {
            Text(
                "HamroThrift",
                style = TextStyle(
                    brush = Brush.linearGradient(colors = listOf(White, deepBlue, Black)),
                    fontSize = 25.sp,
                    fontStyle = FontStyle.Italic,
                    fontFamily = font
                )
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = appBar),
        actions = {
            IconButton(onClick = {
                val intent = Intent(context, CartActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = White)
            }
            IconButton(onClick = {
                val intent = Intent(context, SearchActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = White)
            }
        }
    )
}

@Composable
fun ProductCard(
    product: ProductModel,
    isSmall: Boolean,
    onMessageClick: (ProductModel) -> Unit
) {
    val context = LocalContext.current
    var showDetails by remember { mutableStateOf(false) }
    val cartRepository = remember { CartRepositoryImpl(context) }
    var isAdding by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val modifier = if (isSmall) {
        Modifier.width(120.dp).height(160.dp)
    } else {
        Modifier.fillMaxWidth().height(200.dp)
    }

    Card(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { showDetails = true },
        colors = CardDefaults.cardColors(containerColor = card)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
            )

            Column(
                modifier = Modifier
                    .background(text)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = product.name,
                            color = White,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            fontSize = if (isSmall) 14.sp else 16.sp
                        )
                        Text(
                            text = "Rs.${product.price}",
                            color = White,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                            fontSize = if (isSmall) 12.sp else 14.sp
                        )
                    }
                    Row {
                        IconButton(
                            onClick = { onMessageClick(product) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Filled.Email,
                                contentDescription = "Message Seller",
                                tint = White
                            )
                        }
                        IconButton(
                            onClick = {
                                if (!isAdding) {
                                    val user = FirebaseAuth.getInstance().currentUser
                                    if (user == null) {
                                        Toast.makeText(
                                            context,
                                            "Please log in to add to cart",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@IconButton
                                    }
                                    isAdding = true
                                    coroutineScope.launch {
                                        cartRepository.addToCart(product, 1).collect { success ->
                                            Toast.makeText(
                                                context,
                                                if (success) "Added to cart!" else "Failed to add to cart",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            isAdding = false
                                        }
                                    }
                                }
                            },
                            enabled = !isAdding
                        ) {
                            if (isAdding) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = White
                                )
                            } else {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Add to Cart",
                                    tint = White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDetails) {
        AlertDialog(
            onDismissRequest = { showDetails = false },
            title = { Text(product.name) },
            text = {
                Column {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Price: Rs.${product.price}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text("Category: ${product.category}")
                    Text("Condition: ${product.condition}")
                    Text("Description: ${product.description}")
                    if (product.isOnSale) {
                        Text("On Sale!", color = Color.Red)
                        product.originalPrice?.let {
                            Text("Original Price: Rs.$it")
                        }
                        product.discount?.let {
                            Text("Discount: $it%")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetails = false }) {
                    Text("Close")
                }
            }
        )
    }
}