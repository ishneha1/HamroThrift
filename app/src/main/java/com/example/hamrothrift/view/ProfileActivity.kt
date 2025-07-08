package com.example.hamrothrift.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.hamrothrift.model.NavigationItem
import com.example.hamrothrift.view.screens.ProfileScreen


class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                ProfileContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent() {
    var selectedTab by remember { mutableStateOf(3) }
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    NavigationItem(label = "Home", icon = Icons.Default.Home, index = 0),
                    NavigationItem(label = "Sale", icon = Icons.Default.ShoppingCart, index = 1),
                    NavigationItem(label = "Notification", icon = Icons.Default.Notifications, index = 2),
                    NavigationItem(label = "Profile", icon = Icons.Default.Person, index = 3)
                )

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            when (index) {
                                0 -> navController.navigate("home")
                                1 -> navController.navigate("sale")
                                2 -> navController.navigate("notification")
                                3 -> navController.navigate("profile")
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                3 -> ProfileScreen(navController = navController)
                else -> {}
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewProfileContent() {
    ProfileContent()
}