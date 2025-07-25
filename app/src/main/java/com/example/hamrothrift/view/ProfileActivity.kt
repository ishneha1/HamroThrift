package com.example.hamrothrift.view

import android.app.Activity
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamrothrift.R
import com.example.hamrothrift.repository.UserRepoImpl
import com.example.hamrothrift.view.components.CommonBottomBar
import com.example.hamrothrift.view.components.CommonBottomBarSell
import com.example.hamrothrift.view.screens.ProfileScreen
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.UserViewModel
import android.content.Intent
import androidx.compose.ui.graphics.Color
import com.example.hamrothrift.view.buy.CartActivity
import com.example.hamrothrift.view.buy.DashboardActivityBuy
import com.example.hamrothrift.view.buy.SaleActivity
import com.example.hamrothrift.view.buy.SearchActivity
import com.example.hamrothrift.view.sell.DashboardSellActivity
import com.example.hamrothrift.view.sell.UploadActivity
import com.example.hamrothrift.viewmodel.UserViewModelFactory

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userRepository = UserRepoImpl()
            val viewModel: UserViewModel = viewModel(
                factory = UserViewModelFactory(userRepository)
            )
            val mode = intent.getStringExtra("mode") ?: "buy" // Default to buy mode
            ProfileActivityBody(viewModel, mode)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileActivityBody(viewModel: UserViewModel, mode: String) {
    var selectedTab by remember { mutableStateOf(3) }
    val context = LocalContext.current
    val gradientColors = listOf(White, deepBlue, Black)
    val font = FontFamily(Font(R.font.handmade))
    val activity = context as? Activity

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "HamroThrift",
                        style = TextStyle(
                            brush = Brush.linearGradient(colors = gradientColors),
                            fontSize = 25.sp,
                            fontFamily = font,
                            fontStyle = FontStyle.Italic
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appBar),
                actions = {
                    IconButton(onClick = {
                        val intent =
                            Intent(context, CartActivity::class.java)
                        context.startActivity(intent)}) {
                        Icon(Icons.Default.ShoppingCart, "Cart", tint = Color.White)
                    }
                    IconButton(onClick = {
                        val intent =
                            Intent(context, SearchActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Search, "Search", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            if (mode == "sell") {
                CommonBottomBarSell(
                    selectedTab = selectedTab,
                    onTabSelected = { index ->
                        selectedTab = index
                        when (index) {
                            0 -> {
                                context.startActivity(Intent(context, DashboardSellActivity::class.java))
                                activity?.finish()
                            }
                            1 -> {
                                context.startActivity(Intent(context, UploadActivity::class.java))
                                activity?.finish()
                            }
                            2 -> {
                                val intent = Intent(context, NotificationActivity::class.java)
                                intent.putExtra("mode", mode)
                                context.startActivity(intent)
                                activity?.finish()
                            }
                            3 -> {
                                val intent = Intent(context, ProfileActivity::class.java)
                                intent.putExtra("mode", mode)
                                context.startActivity(intent)
                                activity?.finish()
                            }
                        }
                    }
                )
            } else {
                CommonBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { index ->
                        selectedTab = index
                        when (index) {
                            0 -> {
                                context.startActivity(Intent(context, DashboardActivityBuy::class.java))
                                activity?.finish()
                            }
                            1 -> {
                                context.startActivity(Intent(context, SaleActivity::class.java))
                                activity?.finish()
                            }
                            2 -> {
                                val intent = Intent(context, NotificationActivity::class.java)
                                intent.putExtra("mode", mode)
                                context.startActivity(intent)
                                activity?.finish()
                            }
                            3 -> {
                                val intent = Intent(context, ProfileActivity::class.java)
                                intent.putExtra("mode", mode)
                                context.startActivity(intent)
                                activity?.finish()
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ProfileScreen(viewModel = viewModel)
        }
    }
}