package com.example.hamrothrift.view.buy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import com.example.hamrothrift.R
import com.example.hamrothrift.view.theme.White
import com.example.hamrothrift.view.theme.appBar
import com.example.hamrothrift.view.theme.bg
import com.example.hamrothrift.view.theme.buttton
import com.example.hamrothrift.view.theme.card
import com.example.hamrothrift.view.theme.deepBlue
import com.example.hamrothrift.view.theme.text

class SaleActivity : ComponentActivity() {
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
    SaleActivityBody()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleActivityBody() {
    data class NavItem(val label: String, val icon: ImageVector)

    val navItems = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Search", Icons.Filled.Star),
        NavItem("Sale", Icons.Default.Notifications),
        NavItem("Notification", Icons.Default.Person)
    )
    val gradientColors = listOf(White, deepBlue,Black)
    val font = FontFamily(
        Font(R.font.handmade)
    )


    var selectedTab by remember { mutableIntStateOf(1) }
    Scaffold(
        topBar = {
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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardActivityBuy()
                1 -> SaleActivity()
                2 -> NotificationActivity()
                3 -> ProfileActivity()
            }
        }
    }
}

@Composable
fun SaleScreen() {
    val font = FontFamily(
        Font(R.font.font)
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = bg)
            .padding(16.dp)
    ) {
        item {
            Button(
                onClick = { /* TODO: Buy click */ },
                colors = ButtonDefaults.buttonColors(containerColor = buttton),
            ) {
                Text("Buy", color = Color.White, fontFamily = font, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "HOT SALE",
                color = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font
            )

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

            Text(
                "For You", color = text, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                fontFamily = font
            )

            Spacer(modifier = Modifier.height(15.dp))

            Column {
                repeat(1000) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(3) {
                            LargeProductCard()
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

    }
}

@Composable
fun SmallProductCard() {
    Column(
        modifier = Modifier
            .width(100.dp)
            .height(140.dp)
            .background(card, shape = RoundedCornerShape(10.dp)),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(
                    color = text,
                    shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Name", fontWeight = FontWeight.Bold, color = Color.White)
                Text("Rs.XX", fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}

@Composable
fun LargeProductCard() {
    Column(
        modifier = Modifier
            .width(100.dp)
            .height(140.dp)
            .background(card, shape = RoundedCornerShape(10.dp)),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(
                    text,
                    shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Name", fontWeight = FontWeight.Bold, color = Color.White)
                Text("Rs.XX", fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun SaleActivityPreview() {
    SaleActivityBody()
}