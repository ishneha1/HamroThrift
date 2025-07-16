package com.example.hamrothrift.view.components

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrothrift.R
import com.example.hamrothrift.navigation.NavigationHandler
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.NavigationViewModel

@Composable
fun CommonBottomBar(
    navigationViewModel: NavigationViewModel,
    selectedTab: Int
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val font = FontFamily(Font(R.font.handmade))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
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
            navigationViewModel.navigationItems.forEach { item ->
                BottomNavItem(
                    icon = item.icon,
                    label = item.label,
                    isSelected = selectedTab == item.index,
                    onClick = {
                        navigationViewModel.updateTab(item.index)
                        NavigationHandler.navigateToScreen(context, activity, item.index)
                    },

                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
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