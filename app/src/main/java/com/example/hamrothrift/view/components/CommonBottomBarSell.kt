package com.example.hamrothrift.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrothrift.R
import com.example.hamrothrift.view.theme.ui.theme.*

@Composable
fun CommonBottomBarSell(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val font = FontFamily(Font(R.font.handmade))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = card),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Analytics Tab
            BottomNavItem(
                icon = Icons.Default.Analytics,
                label = "Home",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                font = font
            )

            // Upload Tab
            BottomNavItem(
                icon = Icons.Default.Add,
                label = "Upload",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                font = font
            )


            // Notifications Tab
            BottomNavItem(
                icon = Icons.Default.Notifications,
                label = "Notifications",
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) },
                font = font
            )

            // Profile Tab
            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Profile",
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) },
                font = font
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    font: FontFamily
) {
    IconButton(onClick = onClick) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) buttton else text.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) buttton else text.copy(alpha = 0.6f),
            )
        }
    }
}