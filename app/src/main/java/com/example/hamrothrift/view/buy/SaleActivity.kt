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
import com.example.hamrothrift.view.components.CommonBottomBar
import com.example.hamrothrift.view.components.CommonTopAppBar
import com.example.hamrothrift.view.components.ModeSelectorDropdown
import com.example.hamrothrift.view.components.ProductCard
import com.example.hamrothrift.view.sell.DashboardSellActivity
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.ProductViewModel
import com.example.hamrothrift.viewmodel.ProductViewModelFactory

class SaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = ProductRepoImpl()
            val viewModel: ProductViewModel = viewModel(
                factory = ProductViewModelFactory(repository)
            )
            SaleActivityBody(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleActivityBody(viewModel: ProductViewModel) {
    var selectedTab by remember { mutableIntStateOf(1) }
    val context = LocalContext.current
    val activity = context as? Activity

    val products by viewModel.products.collectAsState(initial = emptyList<ProductModel>())

    val isLoading by viewModel.isLoading.collectAsState(initial = false)

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
//                        error != null -> {
//                            Text(
//                                text = error ?: "Unknown error occurred",
//                                color = MaterialTheme.colorScheme.error,
//                                modifier = Modifier.padding(16.dp)
//                            )
//                        }
                        else -> {
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
//                                items(hotSaleProducts) { product ->
//                                    ProductCard(
//                                        product = product,
//                                        isSmall = true
//                                    )
//                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                "For You",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = text
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            products.forEach { product ->
                                ProductCard(product = product,
                                    isSmall = false,
                                    onMessageClick = { /* Handle message click */ })
                                    //product = product,
                                    //isSmall = false

                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewSaleActivity() {
    val repository = ProductRepoImpl()
    val viewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(repository)
    )
    SaleActivityBody(viewModel)
}