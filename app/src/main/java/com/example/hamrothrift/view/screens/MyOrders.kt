package com.example.hamrothrift.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun MyOrdersScreen() {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var orders by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("orders")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                orders = snapshot.documents.map { it.data ?: emptyMap() }
            } catch (e: Exception) {
                Log.e("MyOrdersScreen", "Error fetching orders", e)
            } finally {
                loading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "My Orders",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFA4CCD9)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        } else if (orders.isEmpty()) {
            Text("No orders found.")
        } else {
            LazyColumn {
                items(orders) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F1F5))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Order ID: ${order["orderId"]}", fontWeight = FontWeight.Bold)
                            Text("Product: ${order["productName"]}")
                            Text("Quantity: ${order["quantity"]}")
                            Text("Total Price: Rs. ${order["totalPrice"]}")
                        }
                    }
                }
            }
        }
    }
}