package com.example.hamrothrift.view.buy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.hamrothrift.R
import com.example.hamrothrift.view.sell.DashboardSellActivity
import com.example.hamrothrift.view.theme.ui.theme.White
import com.example.hamrothrift.view.theme.ui.theme.appBar
import com.example.hamrothrift.view.theme.ui.theme.buttton
import com.example.hamrothrift.view.theme.ui.theme.deepBlue
import com.example.hamrothrift.view.theme.ui.theme.text
import com.example.hamrothrift.view.ProfileActivity

class DashboardActivityBuy : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardActivityBuyBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardActivityBuyBody() {
    data class NavItem(val label: String, val icon: ImageVector)

    val navItems = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Sale", Icons.Default.Star),
        NavItem("Notification", Icons.Filled.Notifications),
        NavItem("Profile", Icons.Default.Person)
    )

    val gradientColors = listOf(White, deepBlue,Black)
    val font = FontFamily(
        Font(R.font.handmade)
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("Select Option") }
    val options = listOf("Buy Mode","Sell Mode")
    var textFieldSize by remember { mutableStateOf(Size.Zero)}


    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HamroThrift"
                    ,style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = gradientColors
                        ),
                        fontSize = 25.sp,
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
        LazyColumn(modifier = Modifier
            .padding(top= 80.dp, start = 20.dp, end = 20.dp)){
            item {
                OutlinedTextField(
                    value = selectedOptionText,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size.toSize()

                        }
                        .clickable {
                            expanded = true
                        }
                        .clip(RoundedCornerShape(10.dp)),

                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = buttton,
                        disabledIndicatorColor = Black,
                        disabledTextColor = Black
                    ),
                    placeholder = {
                        Text(
                            "Select Mode", fontSize = 20.sp, fontWeight = FontWeight.SemiBold
                        )
                    },
                    enabled = false,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }

                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(
                            with(LocalDensity.current)
                            { textFieldSize.width.toDp() })
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedOptionText = option
                                expanded = false
//                                if (option == "Buy Mode") {
//                                    val intent = Intent(context, DashboardActivityBuy::class.java)
//                                    context.startActivity(intent)
//                                    activity?.finish()
//                                }
                                if (option == "Sell Mode") {
                                    val intent =
                                        Intent(context, DashboardSellActivity::class.java)
                                    context.startActivity(intent)
                                    activity?.finish()
                                }
                            }
                        )
                    }
                }


                Text("Top Sellers", color = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Italic,)

                LazyRow(
                    modifier = Modifier
                        .padding(top=20.dp, bottom = 20.dp)
                        .fillMaxWidth()
                        .background(Color.DarkGray,
                            shape = RoundedCornerShape(16.dp)),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
//                    items(sellers) { seller ->
//                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//
//
//                        }
//                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DashboardActivityBuyPreview() {
    DashboardActivityBuyBody()
}