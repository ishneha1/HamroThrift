package com.example.hamrothrift.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrothrift.R
import com.example.hamrothrift.model.Order
import com.example.hamrothrift.view.theme.ui.theme.*

@Composable
fun OrderCard(
    order: Order,
    showBuyerInfo: Boolean = false // Flag to show/hide buyer info for different screens
) {
    val font = FontFamily(Font(R.font.handmade))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = card),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order #${order.orderId.take(8)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = text,
                    fontFamily = font
                )
                Text(
                    text = order.status,
                    fontSize = 14.sp,
                    color = when (order.status.lowercase()) {
                        "delivered" -> Color(0xFF4CAF50)
                        "pending" -> Color(0xFFFF9800)
                        "cancelled" -> Color(0xFFF44336)
                        else -> text
                    },
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Show buyer info only for AllOrders screen
            if (showBuyerInfo && order.buyerName.isNotEmpty()) {
                Text(
                    text = "Buyer: ${order.buyerName}",
                    fontSize = 14.sp,
                    color = text,
                    fontFamily = font
                )
            }

            // Use itemName if available, otherwise use productName
            val displayName = if (order.itemName.isNotEmpty()) order.itemName else order.productName
            if (displayName.isNotEmpty()) {
                Text(
                    text = if (showBuyerInfo) "Item: $displayName" else "Product: $displayName",
                    fontSize = 14.sp,
                    color = text,
                    fontFamily = font
                )
            }

            if (order.quantity > 0) {
                Text(
                    text = "Quantity: ${order.quantity}",
                    fontSize = 14.sp,
                    color = text,
                    fontFamily = font
                )
            }

            // Use price if available, otherwise use totalPrice
            val displayPrice = if (order.price > 0) order.price else order.totalPrice
            if (displayPrice > 0) {
                Text(
                    text = "Total: Rs. ${String.format("%.2f", displayPrice)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = buttton,
                    fontFamily = font
                )
            }

            if (order.orderDate.isNotEmpty()) {
                Text(
                    text = "Date: ${order.orderDate}",
                    fontSize = 12.sp,
                    color = text.copy(alpha = 0.7f),
                    fontFamily = font
                )
            }
        }
    }
}