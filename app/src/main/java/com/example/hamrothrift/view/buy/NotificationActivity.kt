package com.example.hamrothrift.view.buy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrothrift.R
import com.example.hamrothrift.view.theme.Teal
import com.example.hamrothrift.view.theme.White
import com.example.hamrothrift.view.theme.appBar
import com.example.hamrothrift.view.theme.bg
import com.example.hamrothrift.view.theme.deepBlue
import com.example.hamrothrift.view.theme.text

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotificationActivityBody()

        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationActivityBody() {
    data class NavItem(val label: String, val icon: ImageVector)

    val navItems = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Search", Icons.Filled.Star),
        NavItem("Sale", Icons.Default.Notifications),
        NavItem("Notification", Icons.Default.Person)
    )
    val gradientColors = listOf(White, deepBlue, Color.Companion.Black)
    var selectedTab by remember { mutableStateOf(2) }
    val notifications = remember {
        mutableStateListOf(
            NotificationItem(
                title = "Order Shipped",
                message = "Your order #1234 has been shipped.",
                time = "2 hrs ago",
                icon = Icons.Default.CheckCircle
            ),
            NotificationItem(
                title = "Message Received",
                message = "Seller replied to your question.",
                time = "5 hrs ago",
                icon = Icons.Default.Email
            ),
            NotificationItem(
                title = "New Offer",
                message = "Up to 50% off on denim jackets!",
                time = "2 days ago",
                icon = Icons.Default.Star
            )
        )
    }
    val font = FontFamily(
        Font(R.font.handmade)
    )
    Scaffold(
        topBar= {
            TopAppBar(
                title = { Text("HamroThrift"
                    ,style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = gradientColors
                        ),
                        fontSize = 30.sp,
                        fontFamily = font, fontStyle = FontStyle.Italic
                    )) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appBar),
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
            NavigationBar(containerColor = appBar) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(innerPadding)
                .padding(start = 15.dp, end = 15.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notifications",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = text
                )
                Row {
                    TextButton(onClick = {
                        notifications.clear()
                    }) {
                        Text("Clear All",
                            fontSize = 15.sp,
                            color = text)
                    }

                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text("No notifications", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notifications) { notification ->
                        NotificationCard(notification)
                    }
                }
            }
        }

    }


}

data class NotificationItem(
    val title: String,
    val message: String,
    val time: String,
    val icon: ImageVector
)

@Composable
fun NotificationCard(notification: NotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Teal, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = notification.icon,
            contentDescription = notification.title,
            tint = Color.Black,
            modifier = Modifier.size(35.dp)
        )

        Spacer(modifier = Modifier.width(18.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                fontWeight = FontWeight.Bold,
                fontSize =19.sp,
                color = Color.Black
            )
            Text(
                text = notification.message,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Text(
                text = notification.time,
                fontSize = 15.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun NotificationActivityPreview() {
    NotificationActivityBody()
    //NotificationCard(notification = NotificationItem)

}