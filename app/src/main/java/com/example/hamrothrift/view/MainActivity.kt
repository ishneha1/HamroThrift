package com.example.hamrothrift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HamroThriftApp()
        }
    }
}

@Composable
fun HamroThriftApp() {
    ExploreNavigation()
}

// Navigation and Top-Bottom bar setup
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreNavigation() {
    data class NavItem(val label: String, val icon: ImageVector)

    val navItems = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Search", Icons.Default.Search),
        NavItem("Profile", Icons.Default.Person),
        NavItem("Notification", Icons.Default.Notifications)
    )

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hamro Thrift") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF000000)),
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (selectedTab) {
                0 -> ExploreScreen()
                1 -> SearchPlaceholder()
                2 -> ProfilePlaceholder()
                3 -> NotificationPlaceholder()
            }
        }
    }
}

// Main Explore Screen content
@Composable
fun ExploreScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0E0E0))
            .padding(16.dp)
    ) {
        Button(
            onClick = { /* TODO: Buy click */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("Buy", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("HOT SALE", color = Color(0xFF4CAF50), fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(3) {
                SmallProductCard()
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("For You", color = Color(0xFF8BC34A), fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(10.dp))

        Column {
            repeat(2) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(3) {
                        LargeProductCard()
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

// HOT SALE style product
@Composable
fun SmallProductCard() {
    Column(
        modifier = Modifier
            .width(100.dp)
            .height(140.dp)
            .background(Color(0xFF9CCC65), shape = RoundedCornerShape(10.dp)),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color(0xFF00E676), shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Name", fontWeight = FontWeight.Bold, color = Color.Blue)
                Text("Rs.XX", fontWeight = FontWeight.SemiBold, color = Color.Blue)
            }
        }
    }
}

// FOR YOU style product
@Composable
fun LargeProductCard() {
    Column(
        modifier = Modifier
            .width(100.dp)
            .height(140.dp)
            .background(Color(0xFFE6EE9C), shape = RoundedCornerShape(10.dp)),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color(0xFF8BC34A), shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Name", fontWeight = FontWeight.Bold, color = Color.Blue)
                Text("Rs.XX", fontWeight = FontWeight.SemiBold, color = Color.Blue)
            }
        }
    }
}

// Placeholder Composables for other pages
@Composable fun SearchPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Search Screen")
    }
}

@Composable fun ProfilePlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Profile Screen")
    }
}

@Composable fun NotificationPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Notifications Screen")
    }
}

@Preview(showBackground = true)
@Composable
fun ExplorePreview() {
    ExploreNavigation()
}



