package com.example.hamrothrift.view.buy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
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
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.repository.ProductRepoImpl
import com.example.hamrothrift.view.NotificationActivity
import com.example.hamrothrift.view.ProfileActivity
import com.example.hamrothrift.view.components.*
import com.example.hamrothrift.view.sell.DashboardSellActivity
import com.example.hamrothrift.view.theme.ui.theme.bg
import com.example.hamrothrift.view.theme.ui.theme.buttton
import com.example.hamrothrift.view.theme.ui.theme.card
import com.example.hamrothrift.view.theme.ui.theme.text
import com.example.hamrothrift.viewmodel.ProductViewModel
import com.example.hamrothrift.viewmodel.ProductViewModelFactory

class DashboardActivityBuy : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val productRepository = ProductRepoImpl()
            val viewModel: ProductViewModel = viewModel(
                factory = ProductViewModelFactory(productRepository)
            )
            DashboardBuyBody(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBuyBody(viewModel: ProductViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val activity = context as? Activity
    val gridState = rememberLazyGridState()
    val products by viewModel.products.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val font = FontFamily(Font(R.font.handmade))

    var showMessageDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<ProductModel?>(null) }

    val modes = listOf("Sell", "Buy")
    var selectedMode by remember { mutableStateOf("Buy") }

    LaunchedEffect(Unit) {
        viewModel.loadInitialProducts()
    }

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex }
            .collect { firstVisibleItem ->
                if (firstVisibleItem > products.size - 10) {
                    viewModel.loadMoreProducts()
                }
            }
    }

    Scaffold(
        topBar = { CommonTopAppBar() },
        bottomBar = {
            CommonBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { index ->
                    selectedTab = index
                    when (index) {
                        0 -> {
                            // Already on Buy dashboard - do nothing
                        }
                        1 -> {
                            context.startActivity(Intent(context, SaleActivity::class.java))
                            activity?.finish()
                        }
                        2 -> {
                            context.startActivity(Intent(context, NotificationActivity::class.java))
                            activity?.finish()
                        }
                        3 -> {
                            context.startActivity(Intent(context, ProfileActivity::class.java))
                            activity?.finish()
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Buy Products",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = text,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
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
                                        selectedMode = mode
                                        when (mode) {
                                            "Buy" -> {
                                                // Already on Buy dashboard - do nothing
                                            }
                                            "Sell" -> {
                                                val intent = Intent(context, DashboardSellActivity::class.java)
                                                context.startActivity(intent)
                                                activity?.finish()
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


                Spacer(modifier = Modifier.height(18.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = gridState,
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.height(600.dp)
                ) {
                    items(products) { product ->
                        ProductCard(
                            product = product,
                            isSmall = false,
                            onMessageClick = {
                                selectedProduct = it
                                showMessageDialog = true
                            }
                        )
                    }
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = buttton
                        )
                    }
                }
            }
        }

        if (showMessageDialog && selectedProduct != null) {
            MessageDialog(
                product = selectedProduct!!,
                onDismiss = {
                    showMessageDialog = false
                    selectedProduct = null
                }
            )
        }
    }
}