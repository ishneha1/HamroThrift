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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.repository.ProductRepoImpl
import com.example.hamrothrift.repository.NotificationRepoImpl
import com.example.hamrothrift.view.components.CommonBottomBar
import com.example.hamrothrift.view.components.CommonTopAppBar
import com.example.hamrothrift.view.components.ModeSelectorDropdown
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
                    ModeSelectorDropdown(
                        currentMode = "Buy Mode",
                        onModeSelected = { mode ->
                            if (mode == "Sell Mode") {
                                val intent = Intent(context, DashboardSellActivity::class.java)
                                context.startActivity(intent)
                                activity?.finish()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
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
                                    "HOT SALE",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = text
                                )

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    items(hotSaleProducts) { product ->
                                        ProductCard(
                                            product = product,
                                            isSmall = true,
                                            onMessageClick = {
                                                handleMessageClick(
                                                    product,
                                                    notificationViewModel
                                                )
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // All Sale Items Section
                            Text(
                                "Sale Items",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = text
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                // Display all sale products
                if (!isLoading && saleProducts.isNotEmpty()) {
                    items(saleProducts) { product ->
                        ProductCard(
                            product = product,
                            isSmall = false,
                            onMessageClick = {
                                handleMessageClick(
                                    product,
                                    notificationViewModel
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

private fun handleMessageClick(
    product: ProductModel,
    notificationViewModel: NotificationViewModel
) {
    // Send notification to seller
    notificationViewModel.addNotification(
        title = "New Message",
        message = "Someone is interested in your product: ${product.name}",
        type = "message"
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSaleActivity() {
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