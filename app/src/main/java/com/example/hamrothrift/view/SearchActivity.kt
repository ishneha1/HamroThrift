package com.example.hamrothrift.view.buy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.hamrothrift.R
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.repository.SearchRepositoryImpl
import com.example.hamrothrift.view.ProfileActivity
import com.example.hamrothrift.view.components.CommonBottomBar
import com.example.hamrothrift.view.components.CommonTopAppBar
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.SearchViewModel
import com.example.hamrothrift.viewmodel.SearchViewModelFactory

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = SearchRepositoryImpl(this)
            val viewModel: SearchViewModel = viewModel(
                factory = SearchViewModelFactory(repository)
            )
            SearchActivityBody(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchActivityBody(viewModel: SearchViewModel) {
    val context = LocalContext.current
    val font = FontFamily(Font(R.font.handmade))
    val gradientColors = listOf(White, deepBlue, Color.Black)


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
                navigationIcon = {
                    IconButton(onClick = { (context as ComponentActivity).finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
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
                .padding(16.dp)
        ) {
            SearchScreen(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    val font = FontFamily(Font(R.font.handmade))
    val context = LocalContext.current

    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = {
                Text(
                    "Search for products...",
                    fontFamily = font,
                    color = text.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = buttton
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = text.copy(alpha = 0.6f)
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = buttton,
                focusedLabelColor = buttton,
                unfocusedBorderColor = text.copy(alpha = 0.3f),
                unfocusedLabelColor = text.copy(alpha = 0.7f),
                focusedTextColor = text,
                unfocusedTextColor = text,
                unfocusedContainerColor = card,
                focusedContainerColor = card
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Content based on state
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = buttton)
                }
            }

            searchQuery.isEmpty() && searchHistory.isNotEmpty() -> {
                // Show search history
                SearchHistorySection(
                    history = searchHistory,
                    onHistoryClick = { query -> viewModel.selectFromHistory(query) },
                    onClearHistory = { viewModel.clearSearchHistory() },
                    font = font
                )
            }

            searchQuery.isNotEmpty() && searchResults.isEmpty() && !isLoading -> {
                // No results found
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = "No results",
                            tint = text.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No products found",
                            fontFamily = font,
                            fontSize = 18.sp,
                            color = text.copy(alpha = 0.7f)
                        )
                        Text(
                            "Try different keywords",
                            fontFamily = font,
                            fontSize = 14.sp,
                            color = text.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            searchResults.isNotEmpty() -> {
                // Show search results
                SearchResultsSection(
                    results = searchResults,
                    font = font
                )
            }
        }
    }
}

@Composable
fun SearchHistorySection(
    history: List<String>,
    onHistoryClick: (String) -> Unit,
    onClearHistory: () -> Unit,
    font: FontFamily
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Recent Searches",
                fontFamily = font,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = text
            )

            TextButton(onClick = onClearHistory) {
                Text(
                    "Clear All",
                    fontFamily = font,
                    color = buttton
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(history) { query ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onHistoryClick(query) },
                    colors = CardDefaults.cardColors(containerColor = card),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "History",
                            tint = text.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            query,
                            fontFamily = font,
                            color = text,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Default.NorthWest,
                            contentDescription = "Use",
                            tint = text.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultsSection(
    results: List<ProductModel>,
    font: FontFamily
) {
    Column {
        Text(
            "${results.size} products found",
            fontFamily = font,
            fontSize = 14.sp,
            color = text.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn {
            items(results) { product ->
                ProductSearchCard(product = product, font = font)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ProductSearchCard(
    product: ProductModel,
    font: FontFamily
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navigate to product detail */ },
        colors = CardDefaults.cardColors(containerColor = card),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Color.Gray.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontFamily = font,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = text,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rs. ${String.format("%.0f", product.price)}",
                    fontFamily = font,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = buttton
                )

                if (product.category.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.category,
                        fontFamily = font,
                        fontSize = 12.sp,
                        color = text.copy(alpha = 0.6f)
                    )
                }

                if (product.condition.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Condition: ${product.condition}",
                        fontFamily = font,
                        fontSize = 12.sp,
                        color = text.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}