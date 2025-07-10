package com.example.hamrothrift.view.buy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamrothrift.repository.*
import com.example.hamrothrift.view.components.*
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.*

class DashboardActivityBuy : ComponentActivity() {
    private val navigationRepo: NavigationRepo by lazy { NavigationRepoImpl() }
    private val sellerRepo: SellerRepo by lazy { SellerRepoImpl() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navigationViewModel: NavigationViewModel = viewModel(
                factory = NavigationViewModelFactory(navigationRepo)
            )
            val sellerViewModel: SellerViewModel = viewModel(
                factory = SellerViewModelFactory(sellerRepo)
            )
            DashboardActivityBody(navigationViewModel, sellerViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardActivityBody(
    navigationViewModel: NavigationViewModel,
    sellerViewModel: SellerViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val activity = context as? Activity

    val sellers by sellerViewModel.sellers.collectAsState()
    val isLoading by sellerViewModel.isLoading.collectAsState()
    val error by sellerViewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        sellerViewModel.loadSellers()
    }

    Scaffold(
        topBar = { CommonTopAppBar() },
        bottomBar = {
            CommonBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(bg)
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                item {
                    ModeSelectorDropdown(
                        currentMode = "Buy Mode",
                        onModeSelected = { mode ->
                            navigationViewModel.updateMode(mode)
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        error != null -> {
                            Text(
                                text = error ?: "Unknown error occurred",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        else -> {
                            Text(
                                "Popular Sellers",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = text
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            sellers.forEach { seller ->
                                SellerCard(seller = seller)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}