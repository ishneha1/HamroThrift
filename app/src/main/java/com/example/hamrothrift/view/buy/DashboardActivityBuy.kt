package com.example.hamrothrift.view.buy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.repository.ProductRepoImpl
import com.example.hamrothrift.view.ProfileActivity
import com.example.hamrothrift.view.components.*
import com.example.hamrothrift.view.sell.DashboardSellActivity
import com.example.hamrothrift.view.theme.ui.theme.bg
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

    var showMessageDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<ProductModel?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadInitialProducts()
    }

    // Implement infinite scrolling
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
                        1 -> context.startActivity(Intent(context, SaleActivity::class.java))
                        2 -> context.startActivity(Intent(context, NotificationActivity::class.java))
                        3 -> context.startActivity(Intent(context, ProfileActivity::class.java))
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
            Spacer(modifier = Modifier.height(18.dp))


            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = gridState,
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.weight(1f)
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
                        .padding(16.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
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