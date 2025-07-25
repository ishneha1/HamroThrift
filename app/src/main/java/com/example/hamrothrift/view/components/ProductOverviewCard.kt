package com.example.hamrothrift.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.view.theme.ui.theme.Teal

@Composable
fun ProductOverviewCard(product: ProductModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Teal)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Product Image
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(80.dp)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = "Product Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Product Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rs. ${product.price}",
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (product.condition) {
                        "new" -> "New"
                        "likenew" -> "Like New"
                        "good" -> "Good"
                        "fair" -> "Fair"
                        else -> "Used"
                    },
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}