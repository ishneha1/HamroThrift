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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hamrothrift.R
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.repository.CartRepositoryImpl
import com.example.hamrothrift.view.CartActivity
import com.example.hamrothrift.view.buy.SearchActivity
import com.example.hamrothrift.view.theme.ui.theme.*
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
        Modifier.width(120.dp).height(180.dp)
    } else {
        Modifier.fillMaxWidth().height(220.dp)
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
                    .height(if (isSmall) 75.dp else 85.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.name,
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isSmall) 12.sp else 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = "Rs.${product.price}",
                        color = White,
                        fontWeight = FontWeight.Medium,
                        fontSize = if (isSmall) 11.sp else 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onMessageClick(product) },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            Icons.Filled.Email,
                            contentDescription = "Message",
                            tint = White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (!isAdding) {
                                isAdding = true
                                coroutineScope.launch {
                                    try {
                                        cartRepository.addToCart(product, 1).collect { success ->
                                            if (success) {
                                                Toast.makeText(
                                                    context,
                                                    "✓ ${product.name} added to cart!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to add to cart. Please try again.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            isAdding = false
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Error: ${e.message ?: "Unknown error"}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        isAdding = false
                                    }
                                }
                            }
                        },
                        enabled = !isAdding,
                        modifier = Modifier.size(30.dp)
                    ) {
                        if (isAdding) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Add to Cart",
                                tint = White,
                                modifier = Modifier.size(16.dp)
                            )
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
                    Text("Price: Rs.${product.price}", fontWeight = FontWeight.Bold)
                    Text("Category: ${product.category}")
                    Text("Condition: ${product.condition}")
                    Text("Description: ${product.description}")

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (!isAdding) {
                                isAdding = true
                                coroutineScope.launch {
                                    try {
                                        cartRepository.addToCart(product, 1).collect { success ->
                                            if (success) {
                                                Toast.makeText(
                                                    context,
                                                    "✓ ${product.name} added to cart!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                showDetails = false
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to add to cart. Please try again.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            isAdding = false
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Error: ${e.message ?: "Unknown error"}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        isAdding = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isAdding,
                        colors = ButtonDefaults.buttonColors(containerColor = buttton)
                    ) {
                        if (isAdding) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Adding...", color = White)
                        } else {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to Cart", color = White)

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