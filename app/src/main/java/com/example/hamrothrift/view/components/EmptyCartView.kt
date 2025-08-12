package com.example.hamrothrift.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrothrift.view.theme.ui.theme.*

@Composable
fun EmptyCartView(
    modifier: Modifier = Modifier,
    onContinueShopping: () -> Unit
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Empty Cart",
            modifier = Modifier.size(80.dp),
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your cart is empty",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = text,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add some items to get started",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onContinueShopping,
            colors = ButtonDefaults.buttonColors(containerColor = buttton)
        ) {
            Text("Continue Shopping", color = White)
        }
    }
}