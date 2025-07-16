package com.example.hamrothrift.view.sell

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
                            context.startActivity(Intent(context, NotificationActivity::class.java))
                        }
                        3 -> {
                            // Navigate to Profile
                            context.startActivity(Intent(context, ProfileActivity::class.java))
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

            SalesOverviewScreen(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesOverviewScreen(viewModel: SalesOverviewViewModel) {
    val font = FontFamily(Font(R.font.handmade))
    val timeRanges = listOf("7 Days", "30 Days", "90 Days")
    val modes = listOf("Sell", "Buy")

    val salesOverview by viewModel.salesOverview.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedTimeRange by viewModel.selectedTimeRange.collectAsState()
    val selectedMode by viewModel.selectedMode.collectAsState()

    val context = LocalContext.current
    val activity = context as? Activity

    // Load initial data
    LaunchedEffect(Unit) {
        viewModel.loadSalesData("7 Days")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Sales Overview",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = text,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Mode Selector
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = card),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "View Mode",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = text,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(modes) { mode ->
                        FilterChip(
                            onClick = {
                                viewModel.setSelectedMode(mode)
                                // Add navigation logic here
                                when (mode) {
                                    "Buy" -> {
                                        val intent = Intent(context, DashboardActivityBuy::class.java)
                                        context.startActivity(intent)
                                        activity?.finish() // Close current activity
                                    }
                                    "Sell" -> {
                                        // Already on Sell dashboard - just update the mode
                                        // Or navigate to a specific Sell activity if needed
                                    }
                                }
                            },
                            label = {
                                Text(
                                    text = mode,
                                    fontSize = 12.sp
                                )
                            },
                            selected = selectedMode == mode,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = buttton,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }
        // Time Range Selector
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = card),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Time Period",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = text,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(timeRanges) { range ->
                        FilterChip(
                            onClick = { viewModel.loadSalesData(range) },
                            label = {
                                Text(
                                    text = range,
                                    fontSize = 12.sp
                                )
                            },
                            selected = selectedTimeRange == range,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = buttton,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Loading State
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = buttton)
            }
        }

        // Error State
        errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = Color(0xFFF44336)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        color = Color(0xFFC62828),
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Sales Overview Content
        salesOverview?.let { overview ->
            // Summary Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (selectedMode == "Sell") {
                    SummaryCard(
                        title = "Total Sales",
                        value = "${overview.totalSells}",
                        subtitle = "Rs. ${String.format("%.0f", overview.totalSellAmount)}",
                        icon = Icons.Default.TrendingUp,
                        modifier = Modifier.weight(1f)

                    )
                } else {
                    SummaryCard(
                        title = "Total Buys",
                        value = "${overview.totalBuys}",
                        subtitle = "Rs. ${String.format("%.0f", overview.totalBuyAmount)}",
                        modifier = Modifier.weight(1f),
                    )
                }

                SummaryCard(
                    title = "Average",
                    value = if (selectedMode == "Sell") {
                        if (overview.totalSells > 0) "Rs. ${String.format("%.0f", overview.totalSellAmount / overview.totalSells)}" else "Rs. 0"
                    } else {
                        if (overview.totalBuys > 0) "Rs. ${String.format("%.0f", overview.totalBuyAmount / overview.totalBuys)}" else "Rs. 0"
                    },
                    subtitle = "Per Transaction",
                    modifier = Modifier.weight(1f),

                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daily Data List
            if (overview.dailyData.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = card),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Daily ${selectedMode} Activity",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = text,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        overview.dailyData.forEach { dayData ->
                            DailyDataItem(
                                data = dayData,
                                mode = selectedMode,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = card),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = title,
                    tint = buttton,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = text,
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = text.copy(alpha = 0.7f),
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = buttton,
            )
        }
    }
}

@Composable
fun DailyDataItem(
    data: com.example.hamrothrift.model.SalesData,
    mode: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = data.date,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = text,
                )
                Text(
                    text = if (mode == "Sell") "${data.sellCount} transactions" else "${data.buyCount} transactions",
                    fontSize = 12.sp,
                    color = text.copy(alpha = 0.7f),
                )
            }

            Text(
                text = "Rs. ${String.format("%.0f", if (mode == "Sell") data.sellAmount else data.buyAmount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = buttton,
            )
        }
    }
}