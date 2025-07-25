package com.example.hamrothrift.view.sell

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.hamrothrift.R
import com.example.hamrothrift.repository.UploadRepositoryImpl
import com.example.hamrothrift.repository.UploadResult
import com.example.hamrothrift.view.ProfileActivity
import com.example.hamrothrift.view.buy.DashboardActivityBuy
import com.example.hamrothrift.view.NotificationActivity
import com.example.hamrothrift.view.components.CommonBottomBarSell
import com.example.hamrothrift.view.components.CommonTopAppBar
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.UploadViewModel
import com.example.hamrothrift.viewmodel.UploadViewModelFactory
import com.example.hamrothrift.viewmodel.ValidationResult

class UploadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = UploadRepositoryImpl()
            val viewModelFactory = UploadViewModelFactory(repository)
            val viewModel: UploadViewModel = viewModel(factory = viewModelFactory)

            UploadSellBody(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadSellBody(viewModel: UploadViewModel) {
    var selectedTab by remember { mutableStateOf(1) }
    val context = LocalContext.current
    val activity = context as? Activity
    val font = FontFamily(Font(R.font.handmade))
    val modes = listOf("Sell", "Buy")
    val selectedMode by viewModel.selectedMode.collectAsState()

    Scaffold(
        topBar = { CommonTopAppBar() },
        bottomBar = {
            CommonBottomBarSell(
                selectedTab = selectedTab,
                onTabSelected = { index ->
                    selectedTab = index
                    when (index) {
                        0 -> {
                            context.startActivity(Intent(context, DashboardSellActivity::class.java))
                            activity?.finish()
                        }
                        1 -> { /* Already on Upload - do nothing */ }
                        2 -> {
                            val intent = Intent(context, NotificationActivity::class.java)
                            intent.putExtra("mode", "sell")
                            context.startActivity(intent)
                        }
                        3 -> {
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
                                    when (mode) {
                                        "Buy" -> {
                                            val intent = Intent(context, DashboardActivityBuy::class.java)
                                            context.startActivity(intent)
                                            activity?.finish()
                                        }
                                        "Sell" -> {
                                            // Already on Sell dashboard
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

            UploadSellScreen(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadSellScreen(viewModel: UploadViewModel) {
    val context = LocalContext.current
    val font = FontFamily(Font(R.font.handmade))

    // Form state
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val name = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("") }
    val price = remember { mutableStateOf("") }
    val condition = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    var isOnSale by remember { mutableStateOf(false) }
    var originalPrice = remember { mutableStateOf("") }
    var discount = remember { mutableStateOf("") }

    // ViewModel state
    val uploadState by viewModel.uploadState.collectAsState()

    // Track if we've shown success state
    var hasShownSuccess by remember { mutableStateOf(false) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri
    }

    // Handle upload state changes
    LaunchedEffect(uploadState) {
        val currentState = uploadState
        when (currentState) {
            is UploadResult.Success -> {
                // Show success toast immediately
                Toast.makeText(
                    context,
                    "Product '${currentState.product.name}' uploaded successfully!",
                    Toast.LENGTH_LONG
                ).show()

                // Wait a bit to show the success state, then reset
                kotlinx.coroutines.delay(1500)

                // Reset form
                imageUri.value = null
                name.value = ""
                category.value = ""
                price.value = ""
                condition.value = ""
                description.value = ""
                isOnSale = false
                originalPrice.value = ""
                discount.value = ""

                // Clear the upload state
                viewModel.clearUploadState()
            }
            is UploadResult.Error -> {
                Toast.makeText(
                    context,
                    "Upload failed: ${currentState.message}",
                    Toast.LENGTH_LONG
                ).show()
                // Don't delay for error, clear immediately
                kotlinx.coroutines.delay(500)
                viewModel.clearUploadState()
            }
            is UploadResult.Loading -> {
                // Do nothing, just show loading state
            }
            null -> {
                // Do nothing for null state
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Add Your Product",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = text,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Image Upload Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { imagePickerLauncher.launch("image/*") },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri.value != null) {
                    AsyncImage(
                        model = imageUri.value,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Add Photo",
                            modifier = Modifier.size(48.dp),
                            tint = buttton
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap to add photo",
                            fontSize = 16.sp,
                            color = text.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Product Name
        OutlinedTextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("Product Name", color = text) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = buttton,
                focusedLabelColor = buttton,
                unfocusedBorderColor = text.copy(alpha = 0.3f),
                unfocusedLabelColor = text.copy(alpha = 0.7f),
                focusedTextColor = text,
                unfocusedTextColor = text,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Dropdown
        var expandedCategory by remember { mutableStateOf(false) }
        val categories = listOf("Electronics", "Clothing", "Books", "Home & Garden", "Shoes","Sports", "Others")

        ExposedDropdownMenuBox(
            expanded = expandedCategory,
            onExpandedChange = { expandedCategory = it }
        ) {
            OutlinedTextField(
                value = category.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category", color = text) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = buttton,
                    focusedLabelColor = buttton,
                    unfocusedBorderColor = text.copy(alpha = 0.3f),
                    unfocusedLabelColor = text.copy(alpha = 0.7f),
                    focusedTextColor = text,
                    unfocusedTextColor = text,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
            ExposedDropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { expandedCategory = false },
                modifier = Modifier.background(Color.White)
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat, color = text) },
                        onClick = {
                            category.value = cat
                            expandedCategory = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Price
        OutlinedTextField(
            value = price.value,
            onValueChange = { price.value = it },
            label = { Text("Price (Rs.)", color = text) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = buttton,
                focusedLabelColor = buttton,
                unfocusedBorderColor = text.copy(alpha = 0.3f),
                unfocusedLabelColor = text.copy(alpha = 0.7f),
                focusedTextColor = text,
                unfocusedTextColor = text,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Condition Dropdown
        var expandedCondition by remember { mutableStateOf(false) }
        val conditions = listOf("New", "Like New", "Good", "Fair", "Poor")

        ExposedDropdownMenuBox(
            expanded = expandedCondition,
            onExpandedChange = { expandedCondition = it }
        ) {
            OutlinedTextField(
                value = condition.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Condition", color = text) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCondition) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = buttton,
                    focusedLabelColor = buttton,
                    unfocusedBorderColor = text.copy(alpha = 0.3f),
                    unfocusedLabelColor = text.copy(alpha = 0.7f),
                    focusedTextColor = text,
                    unfocusedTextColor = text,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
            ExposedDropdownMenu(
                expanded = expandedCondition,
                onDismissRequest = { expandedCondition = false },
                modifier = Modifier.background(Color.White)
            ) {
                conditions.forEach { cond ->
                    DropdownMenuItem(
                        text = { Text(cond, color = text) },
                        onClick = {
                            condition.value = cond
                            expandedCondition = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        OutlinedTextField(
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text("Description", color = text) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 4,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = buttton,
                focusedLabelColor = buttton,
                unfocusedBorderColor = text.copy(alpha = 0.3f),
                unfocusedLabelColor = text.copy(alpha = 0.7f),
                focusedTextColor = text,
                unfocusedTextColor = text,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sale Option Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = card),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isOnSale,
                        onCheckedChange = { isOnSale = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = buttton,
                            uncheckedColor = text.copy(alpha = 0.6f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "List item on sale",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = text
                    )
                }

                if (isOnSale) {
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = originalPrice.value,
                        onValueChange = { originalPrice.value = it },
                        label = { Text("Original Price (Rs.)", color = text) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = buttton,
                            focusedLabelColor = buttton,
                            unfocusedBorderColor = text.copy(alpha = 0.3f),
                            unfocusedLabelColor = text.copy(alpha = 0.7f),
                            focusedTextColor = text,
                            unfocusedTextColor = text,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = discount.value,
                        onValueChange = { discount.value = it },
                        label = { Text("Discount (%)", color = text) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = buttton,
                            focusedLabelColor = buttton,
                            unfocusedBorderColor = text.copy(alpha = 0.3f),
                            unfocusedLabelColor = text.copy(alpha = 0.7f),
                            focusedTextColor = text,
                            unfocusedTextColor = text,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Upload Button
        Button(
            onClick = {
                val validation = viewModel.validateProductData(
                    name = name.value,
                    category = category.value,
                    price = price.value,
                    condition = condition.value,
                    imageUri = imageUri.value
                )

                when (validation) {
                    is ValidationResult.Valid -> {
                        viewModel.uploadProduct(
                            context = context,
                            imageUri = imageUri.value,
                            name = name.value,
                            category = category.value,
                            price = price.value,
                            condition = condition.value,
                            description = description.value,
                            isOnSale = isOnSale,
                            originalPrice = if (isOnSale && originalPrice.value.isNotEmpty()) originalPrice.value.toDoubleOrNull() else null,
                            discount = if (isOnSale && discount.value.isNotEmpty()) discount.value.toDoubleOrNull() else null
                        )
                    }
                    is ValidationResult.Invalid -> {
                        Toast.makeText(
                            context,
                            validation.errors.joinToString("\n"),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = when (uploadState) {
                    is UploadResult.Success -> Color(0xFF4CAF50) // Green for success
                    else -> buttton
                },
                disabledContainerColor = buttton.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = uploadState !is UploadResult.Loading
        ) {
            // FIX: Use uploadState directly here instead of capturing it
            when (uploadState) {
                is UploadResult.Loading -> {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Uploading...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                is UploadResult.Success -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Product Added!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                is UploadResult.Error -> {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Upload Product",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                null -> {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Upload Product",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}