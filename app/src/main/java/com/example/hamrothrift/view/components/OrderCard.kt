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
import com.example.hamrothrift.model.BillingAddress
import com.example.hamrothrift.view.theme.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderCard(
    order: Order,
    showBuyerInfo: Boolean = false,
    billingAddress: BillingAddress? = null // Optional billing address parameter
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

            Text(
                text = "User ID: ${order.userId}",
                fontSize = 14.sp,
                color = text,
                fontFamily = font
            )

            // Show billing address if available and showBuyerInfo is true
            if (showBuyerInfo && billingAddress != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Customer: ${billingAddress.name}",
                    fontSize = 14.sp,
                    color = text,
                    fontFamily = font
                )
                if (billingAddress.address.isNotEmpty()) {
                    Text(
                        text = "Address: ${billingAddress.address}, ${billingAddress.city}",
                        fontSize = 14.sp,
                        color = text,
                        fontFamily = font
                    )
                }
                if (billingAddress.phone.isNotEmpty()) {
                    Text(
                        text = "Phone: ${billingAddress.phone}",
                        fontSize = 14.sp,
                        color = text,
                        fontFamily = font
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total: Rs. ${String.format("%.2f", order.totalPrice)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = buttton,
                fontFamily = font
            )

            Text(
                text = "Date: ${formatOrderDate(order.orderDate)}",
                fontSize = 12.sp,
                color = text.copy(alpha = 0.7f),
                fontFamily = font
            )
        }
    }
}

private fun formatOrderDate(timestamp: Long): String {
    return if (timestamp > 0) {
        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        formatter.format(Date(timestamp))
    } else {
        "Unknown date"
    }
}