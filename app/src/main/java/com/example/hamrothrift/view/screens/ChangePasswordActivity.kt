package com.example.hamrothrift.view.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hamrothrift.R
import com.example.hamrothrift.repository.PasswordRepositoryImpl
import com.example.hamrothrift.view.HomepageActivity
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.PasswordChangeViewModel
import com.example.hamrothrift.viewmodel.PasswordChangeViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class ChangePasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = PasswordRepositoryImpl()
            val viewModel: PasswordChangeViewModel = viewModel(
                factory = PasswordChangeViewModelFactory(repository)
            )
            ChangePasswordActivityBody(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordActivityBody(viewModel: PasswordChangeViewModel) {
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
            ChangePasswordScreen(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(viewModel: PasswordChangeViewModel) {
    val user = FirebaseAuth.getInstance().currentUser
    val font = FontFamily(Font(R.font.handmade))

    var showPasswordMethod by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val emailSent by viewModel.emailSent.collectAsState()

    val context = LocalContext.current

    // Logout and navigate to homepage after reset email sent
    LaunchedEffect(emailSent) {
        if (emailSent) {
            delay(2000) // Show message for 2 seconds
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, HomepageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            currentPassword = ""
            newPassword = ""
            confirmPassword = ""
            verificationCode = ""
            viewModel.resetSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Change Password",
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            color = text,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (!showPasswordMethod) {
            // Method Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = card),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Choose Password Change Method",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = text
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            user?.email?.let { email ->
                                viewModel.sendPasswordResetEmail(email)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = buttton),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text(
                            text = if (isLoading) "Sending..." else "Send Reset Email",
                            color = White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showPasswordMethod = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = buttton
                        )
                    ) {
                        Text(
                            text = "Use Current Password"
                        )
                    }
                }
            }
        } else {
            // Current Password Method
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = card),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Enter Current Password",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = text
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showPassword) "Hide password" else "Show password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = buttton,
                            focusedLabelColor = buttton
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showPassword) "Hide password" else "Show password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = buttton,
                            focusedLabelColor = buttton
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm New Password") },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showPassword) "Hide password" else "Show password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = buttton,
                            focusedLabelColor = buttton
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            when {
                                currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() -> {
                                    // Show error message
                                }
                                newPassword != confirmPassword -> {
                                    // Show error message
                                }
                                newPassword.length < 6 -> {
                                    // Show error message
                                }
                                else -> {
                                    viewModel.changePasswordWithAuth(currentPassword, newPassword)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = buttton),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text(
                            text = if (isLoading) "Updating..." else "Change Password",
                            color = White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { showPasswordMethod = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "← Back to Methods",
                            color = buttton
                        )
                    }
                }
            }
        }

        // Email Sent Confirmation
        if (emailSent) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = card),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = Green
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Email Sent!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Green
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Check your email (${user?.email}) for password reset instructions.",
                        fontSize = 14.sp,
                        color = Green
                    )
                }
            }
        }

        // Message Display
        message?.let { msg ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (emailSent || isSuccess) Color(0xFFE8F5E8) else Color(0xFFFFEBEE)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (emailSent || isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = null,
                        tint = if (emailSent || isSuccess) Green else Red
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = msg,
                        color = if (emailSent || isSuccess) Green else Red,
                    )
                }
            }
        }

        if (isLoading) {
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