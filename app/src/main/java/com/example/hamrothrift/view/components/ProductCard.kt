package com.example.hamrothrift.view.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import coil.compose.AsyncImage
import com.example.hamrothrift.R
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.view.theme.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopAppBar() {
    val font = FontFamily(
        Font(R.font.handmade)
    )
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
            IconButton(onClick = {}) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = White)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = White)
            }
        }
    )
}

@Composable
fun CommonBottomBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val items = listOf(
        Triple("Home", Icons.Default.Home, 0),
        Triple("Sale", Icons.Default.Star, 1),
        Triple("Notification", Icons.Default.Notifications, 2),
        Triple("Profile", Icons.Default.Person, 3)
    )

    NavigationBar(containerColor = appBar) {
        items.forEach { (label, icon, index) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = selectedTab == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeSelectorDropdown(
    currentMode: String,
    onModeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val options = listOf("Buy Mode", "Sell Mode")

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = currentMode,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                }
                .clickable { expanded = true }
                .clip(RoundedCornerShape(10.dp)),
            colors = TextFieldDefaults.colors(
                disabledContainerColor = buttton,
                disabledIndicatorColor = Black,
                disabledTextColor = Black
            ),
            enabled = false,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        expanded = false
                        onModeSelected(option)
                    }
                )
            }
        }
    }
}

// Update ProductCard.kt
@Composable
fun ProductCard(
    product: ProductModel,
    isSmall: Boolean,
    onMessageClick: (ProductModel) -> Unit
) {
    val modifier = if (isSmall) {
        Modifier
            .width(120.dp)
            .height(160.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .height(200.dp)
    }

    Card(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(10.dp)),
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
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isSmall) 14.sp else 16.sp
                        )
                        Text(
                            text = "Rs.${product.price}",
                            color = White,
                            fontWeight = FontWeight.Medium,
                            fontSize = if (isSmall) 12.sp else 14.sp
                        )
                    }
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
                }
            }
        }
    }
}

// In your product card or detail screen
@Composable
fun AddToCartButton(
    product: ProductModel,
    cartRepository: CartRepository
) {
    val context = LocalContext.current
    var isAdding by remember { mutableStateOf(false) }

    Button(
        onClick = {
            if (!isAdding) {
                isAdding = true
                // Add to cart logic
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        cartRepository.addToCart(product, 1).collect { success ->
                            withContext(Dispatchers.Main) {
                                if (success) {
                                    Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show()
                                }
                                isAdding = false
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            isAdding = false
                        }
                    }
                }
            }
        },
        enabled = !isAdding,
        colors = ButtonDefaults.buttonColors(containerColor = buttton)
    ) {
        if (isAdding) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = White
            )
        } else {
            Icon(Icons.Default.ShoppingCart, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add to Cart")
        }
    }
}