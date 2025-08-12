package com.example.hamrothrift.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hamrothrift.model.CartItem
import com.example.hamrothrift.view.theme.ui.theme.*

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            AsyncImage(
                model = cartItem.product.imageUrl,
                contentDescription = cartItem.product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Product Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = text,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rs. ${cartItem.product.price}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = buttton
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quantity Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (cartItem.quantity > 1) {
                                onQuantityChange(cartItem.quantity - 1)
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = if (cartItem.quantity > 1) buttton else Color.Gray
                        )
                    }

                    Text(
                        text = cartItem.quantity.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = { onQuantityChange(cartItem.quantity + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = buttton
                        )
                    }
                }
            }

            // Remove Button
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = Color.Red
                )
            }
        }
    }
}
