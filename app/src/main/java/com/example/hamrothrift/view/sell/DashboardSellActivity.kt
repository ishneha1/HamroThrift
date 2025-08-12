package com.example.hamrothrift.view.sell

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamrothrift.R
import com.example.hamrothrift.repository.SalesRepositoryImpl
import com.example.hamrothrift.view.ProfileActivity
import com.example.hamrothrift.view.buy.DashboardActivityBuy
import com.example.hamrothrift.view.NotificationActivity
import com.example.hamrothrift.view.components.CommonBottomBarSell
import com.example.hamrothrift.view.components.CommonTopAppBar
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.SalesOverviewViewModel
import com.example.hamrothrift.viewmodel.SalesOverviewViewModelFactory

class DashboardSellActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = SalesRepositoryImpl()
            val viewModel: SalesOverviewViewModel = viewModel(
                factory = SalesOverviewViewModelFactory(repository)
            )
            DashboardSellBody(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardSellBody(viewModel: SalesOverviewViewModel) {
    var selectedTab by remember { mutableStateOf(0) } // Home tab selected (Analytics/Dashboard)
    val context = LocalContext.current
    val activity = context as? Activity
    val font = FontFamily(Font(R.font.handmade))

    Scaffold(
        topBar = { CommonTopAppBar() },
        bottomBar = {
            CommonBottomBarSell(
                selectedTab = selectedTab,
                onTabSelected = { index ->
                    selectedTab = index
                    when (index) {
                        0 -> { /* Already on Home/Analytics - do nothing */ }
                        1 -> {
                             context.startActivity(Intent(context, UploadActivity::class.java))
                        }
                        2 -> {
                            // Navigate to Notifications
                            val intent = Intent(context, NotificationActivity::class.java)
                            intent.putExtra("mode", "sell")
                            context.startActivity(intent)
                        }
                        3 -> {
                            // Navigate to Profile
                            val intent = Intent(context, ProfileActivity::class.java)
                            intent.putExtra("mode", "sell")
                            context.startActivity(intent)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(bg)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // About Section
            Card(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                colors = CardDefaults.cardColors(containerColor = card),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "About HamroThrift",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = font,
                        color = text,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Canvas(
                        modifier = Modifier
                            .height(2.dp)
                            .width(300.dp)
                    ) {
                        drawLine(
                            color = text.copy(alpha = 0.8f),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = size.height
                        )
                    }
                    Spacer(modifier = Modifier.height(25.dp))

                    val descriptions = listOf(
                        "• Discover unique second-hand treasures at unbeatable prices.",
                        "• List your unused items easily and reach real buyers.",
                        "• Connect with the youth-driven circular fashion movement.",
                        "• Trusted platform for Gen-Z vintage finds and thrift needs."
                    )
                    descriptions.forEach {
                        Text(
                            text = it,
                            fontSize = 20.sp,
                            lineHeight = 25.sp,
                            modifier = Modifier.padding(bottom = 8.dp),
                            color = text,
                            fontStyle = FontStyle.Italic,
                        )
                    }
                }
            }

        }

    }
}

