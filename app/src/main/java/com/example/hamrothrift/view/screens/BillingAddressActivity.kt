package com.example.hamrothrift.view.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamrothrift.R
import com.example.hamrothrift.model.BillingAddress
import com.example.hamrothrift.repository.BillingAddressRepositoryImpl
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.BillingAddressViewModel
import com.example.hamrothrift.viewmodel.BillingAddressViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class BillingAddressActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = BillingAddressRepositoryImpl()
            val viewModel: BillingAddressViewModel = viewModel(
                factory = BillingAddressViewModelFactory(repository)
            )
            BillingAddressActivityBody(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BillingAddressActivityBody(viewModel: BillingAddressViewModel) {
    var selectedTab by remember { mutableStateOf(-1) }
    val context = LocalContext.current
    val gradientColors = listOf(White, deepBlue, Color.Black)
    val font = FontFamily(Font(R.font.handmade))

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(bg)
        ) {
            BillingAddressScreen(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingAddressScreen(viewModel: BillingAddressViewModel) {
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid ?: return
    val font = FontFamily(Font(R.font.handmade))

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }

    val billingAddress by viewModel.billingAddress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    // Load existing billing address
    LaunchedEffect(userId) {
        viewModel.loadBillingAddress(userId)
    }

    // Update form fields when billing address is loaded
    LaunchedEffect(billingAddress) {
        billingAddress?.let { billingData ->
            name = billingData.name
            phone = billingData.phone
            address = billingData.address
            city = billingData.city
            state = billingData.state
            zipCode = billingData.zipCode
        }
    }
    // Reset success state after showing message
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            viewModel.resetSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Billing Address",
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            color = text,
            fontFamily = font,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = card),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name", fontFamily = font) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = buttton,
                        focusedLabelColor = buttton
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Name",
                            tint = buttton
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number", fontFamily = font) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = buttton,
                        focusedLabelColor = buttton
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            tint = buttton
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Street Address", fontFamily = font) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = buttton,
                        focusedLabelColor = buttton
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Address",
                            tint = buttton
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City", fontFamily = font) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = buttton,
                            focusedLabelColor = buttton
                        )
                    )

                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text("State/Province", fontFamily = font) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = buttton,
                            focusedLabelColor = buttton
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = zipCode,
                    onValueChange = { zipCode = it },
                    label = { Text("ZIP/Postal Code", fontFamily = font) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = buttton,
                        focusedLabelColor = buttton
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "ZIP Code",
                            tint = buttton
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val newBillingAddress = BillingAddress(
                                userId = userId,
                                name = name,
                                phone = phone,
                                address = address,
                                city = city,
                                state = state,
                                zipCode = zipCode
                            )
                            viewModel.saveBillingAddress(newBillingAddress)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = buttton),
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading && name.isNotBlank() && phone.isNotBlank() && address.isNotBlank()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = White,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save Address",
                                color = White,
                                fontFamily = font
                            )
                        }
                    }

                    if (billingAddress != null) {
                        OutlinedButton(
                            onClick = {
                                viewModel.deleteBillingAddress(userId)
                                name = ""
                                phone = ""
                                address = ""
                                city = ""
                                state = ""
                                zipCode = ""
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Red
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Delete",
                                fontFamily = font
                            )
                        }
                    }
                }
            }
        }

        // Message Display
        message?.let { msg ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSuccess) Color(0xFFE8F5E8) else Color(0xFFFFEBEE)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = null,
                        tint = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = msg,
                        color = if (isSuccess) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontFamily = font
                    )
                }
            }
        }

        if (isLoading && message == null) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = buttton)
            }
        }
    }
}