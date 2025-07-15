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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamrothrift.R
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.repository.ProductRepoImpl
import com.example.hamrothrift.repository.NotificationRepoImpl
import com.example.hamrothrift.view.components.CommonBottomBar
import com.example.hamrothrift.view.components.CommonTopAppBar
import com.example.hamrothrift.view.components.ProductCard
import com.example.hamrothrift.view.sell.DashboardSellActivity
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.ProductViewModel
import com.example.hamrothrift.viewmodel.ProductViewModelFactory
import com.example.hamrothrift.viewmodel.NotificationViewModel
import com.example.hamrothrift.viewmodel.NotificationViewModelFactory

class SaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val productRepository = ProductRepoImpl()
            val notificationRepository = NotificationRepoImpl()
            val productViewModel: ProductViewModel = viewModel(
                factory = ProductViewModelFactory(productRepository)
            )
            val notificationViewModel: NotificationViewModel = viewModel(
                factory = NotificationViewModelFactory(notificationRepository)
            )
            SaleActivityBody(productViewModel, notificationViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleActivityBody(
    productViewModel: ProductViewModel,
    notificationViewModel: NotificationViewModel
) {
    var selectedTab by remember { mutableIntStateOf(1) }
    val context = LocalContext.current
    val activity = context as? Activity
    val font = FontFamily(Font(R.font.handmade))

    val modes = listOf("Sell", "Buy")
    var selectedMode by remember { mutableStateOf("Buy") }

    val allProducts by productViewModel.products.collectAsState(initial = emptyList<ProductModel>())
    val isLoading by productViewModel.isLoading.collectAsState(initial = false)

    // Filter products to show only items on sale
    val saleProducts = remember(allProducts) {
        allProducts.filter { product ->
            product.isOnSale == true ||
                    (product.originalPrice != null && product.price < (product.originalPrice ?: 0.0)) ||
                    (product.discount != null && product.discount!! > 0)
        }
    }

    // Hot sale products - top 5 sale items sorted by discount
    val hotSaleProducts = remember(saleProducts) {
        saleProducts.sortedByDescending { product ->
            product.discount ?: 0.0
        }.take(5)
    }

    // Function to handle message click
    fun handleMessageClick(product: ProductModel) {
        notificationViewModel.addNotification(
            title = "New Message",
            message = "Someone is interested in your product: ${product.name}",
            type = "message"
        )
    }

    // Fetch products when composable is first created
    LaunchedEffect(Unit) {
        productViewModel.loadInitialProducts()
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
                    Text(
                        text = "Sale Products",
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
                                fontFamily = font,
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
                                                fontFamily = font,
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

                    Spacer(modifier = Modifier.height(20.dp))

                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = buttton)
                            }
                        }
                        saleProducts.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No sale items available",
                                    color = text,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        else -> {
                            // Hot Sale Section
                            if (hotSaleProducts.isNotEmpty()) {
                                Text(
                                    text = "HOT SALE",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = text,
                                    fontFamily = font
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                // Hot Sale Items Row
                if (!isLoading && hotSaleProducts.isNotEmpty()) {
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            items(hotSaleProducts) { product ->
                                ProductCard(
                                    product = product,
                                    isSmall = true,
                                    onMessageClick = { handleMessageClick(product) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "All Sale Items",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = text,
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Display all sale products
                if (!isLoading && saleProducts.isNotEmpty()) {
                    items(saleProducts) { product ->
                        ProductCard(
                            product = product,
                            isSmall = false,
                            onMessageClick = { handleMessageClick(product) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSaleActivity() {
    // Preview composable - simplified for preview
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Sale Activity Preview",
            fontSize = 20.sp,
            color = text
        )
    }
}