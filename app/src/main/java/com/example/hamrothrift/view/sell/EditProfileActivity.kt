package com.example.hamrothrift.view

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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.hamrothrift.R
import com.example.hamrothrift.model.UserModel
import com.example.hamrothrift.repository.UserRepoImpl
import com.example.hamrothrift.view.buy.DashboardActivityBuy
import com.example.hamrothrift.view.buy.NotificationActivity
import com.example.hamrothrift.view.sell.DashboardSellActivity
import com.example.hamrothrift.view.components.CommonBottomBarSell
import com.example.hamrothrift.view.components.CommonTopAppBar
import com.example.hamrothrift.view.sell.UploadActivity
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EditProfileScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen() {
    val context = LocalContext.current
    val activity = context as? Activity
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var selectedTab by remember { mutableStateOf(1) }

    val font = FontFamily(Font(R.font.handmade))
    val repo = remember { UserRepoImpl() }
    val userViewModel = remember { UserViewModel(repo) }

    // State variables
    var isLoading by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf("") }
    var isImageUploading by remember { mutableStateOf(false) }

    val genderOptions = listOf("Male", "Female", "Others")

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            currentUser?.uid?.let { userId ->
                uploadProfileImage(userId, it) { success, message, imageUrl ->
                    if (success) {
                        profileImageUrl = imageUrl ?: ""
                        Toast.makeText(context, "Profile image updated!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                    isImageUploading = false
                }
            }
        }
    }

    // Load user profile when screen opens
    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { userId ->
            email = currentUser.email ?: ""
            userViewModel.getCurrentUserProfile(userId) { user, message ->
                if (user != null) {
                    firstName = user.firstName
                    lastName = user.lastName
                    selectedGender = user.gender
                    // Load profile image URL from Firestore
                    Firebase.firestore.collection("users").document(userId)
                        .get()
                        .addOnSuccessListener { document ->
                            profileImageUrl = document.getString("profileImageUrl") ?: ""
                        }
                } else {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
                isLoading = false
            }
        } ?: run {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_LONG).show()
            isLoading = false
        }
    }

    Scaffold(
        topBar = { CommonTopAppBar() },
        bottomBar = {
            CommonBottomBarSell(
                selectedTab = selectedTab,
                onTabSelected = { index ->
                    selectedTab = index
                    when (index) {
                        0 -> { /* Already on Home/Analytics - do nothing */ }
                        1 -> {
                            context.startActivity(Intent(context, UploadActivity::class.java))
                        }
                        2 -> {
                            // Navigate to Notifications
                            context.startActivity(Intent(context, NotificationActivity::class.java))
                        }
                        3 -> {
                            // Navigate to Profile
                            context.startActivity(Intent(context, ProfileActivity::class.java))
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = buttton)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(bg)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = card),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Edit Your Profile",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = font,
                            color = text,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Profile Image Section
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isImageUploading) {
                                CircularProgressIndicator(color = buttton)
                            } else {
                                val painter = rememberAsyncImagePainter(
                                    model = profileImageUrl.ifEmpty { R.drawable.profilephoto }
                                )

                                Image(
                                    painter = painter,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .border(3.dp, buttton, CircleShape)
                                        .clickable {
                                            isImageUploading = true
                                            imagePickerLauncher.launch("image/*")
                                        },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Text(
                            text = "Tap image to change",
                            fontSize = 12.sp,
                            fontFamily = font,
                            color = text.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = firstName,
                                onValueChange = { firstName = it },
                                label = { Text("First Name", color = text) },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = "Name", tint = buttton)
                                },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
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
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = lastName,
                                onValueChange = { lastName = it },
                                label = { Text("Last Name", color = text) },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = "Name", tint = buttton)
                                },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
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
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Text(
                            text = "Gender",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font,
                            color = text
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            genderOptions.forEach { gender ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    RadioButton(
                                        selected = (gender == selectedGender),
                                        onClick = { selectedGender = gender },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = buttton,
                                            unselectedColor = text.copy(alpha = 0.6f)
                                        )
                                    )
                                    Text(
                                        text = gender,
                                        color = text,
                                        fontFamily = font,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email", color = text) },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = "Email", tint = buttton)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = buttton,
                                focusedLabelColor = buttton,
                                unfocusedBorderColor = text.copy(alpha = 0.3f),
                                unfocusedLabelColor = text.copy(alpha = 0.7f),
                                focusedTextColor = text,
                                unfocusedTextColor = text,
                                unfocusedContainerColor = card,
                                focusedContainerColor = card,
                                disabledBorderColor = text.copy(alpha = 0.2f),
                                disabledTextColor = text.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = false
                        )



                        Button(
                            onClick = {
                                if (userViewModel.validateProfileData(firstName, lastName, selectedGender)) {
                                    isUpdating = true
                                    val updatedUser = UserModel(
                                        userId = currentUser?.uid ?: "",
                                        firstName = firstName,
                                        lastName = lastName,
                                        gender = selectedGender,
                                        email = email,
                                        password = ""
                                    )

                                    userViewModel.updateUserProfile(currentUser?.uid ?: "", updatedUser) { success, message ->
                                        isUpdating = false
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Please fill all fields correctly", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttton,
                                disabledContainerColor = buttton.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isUpdating
                        ) {
                            if (isUpdating) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Updating...",
                                    fontFamily = font,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            } else {
                                Text(
                                    text = "Save Changes",
                                    fontFamily = font,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun uploadProfileImage(
    userId: String,
    imageUri: Uri,
    onResult: (Boolean, String, String?) -> Unit
) {
    val storageRef = Firebase.storage.reference
    val profileImagesRef = storageRef.child("profile_images/$userId.jpg")

    profileImagesRef.putFile(imageUri)
        .addOnSuccessListener {
            profileImagesRef.downloadUrl.addOnSuccessListener { uri ->
                Firebase.firestore.collection("users")
                    .document(userId)
                    .update("profileImageUrl", uri.toString())
                    .addOnSuccessListener {
                        onResult(true, "Profile image updated successfully!", uri.toString())
                    }
                    .addOnFailureListener { e ->
                        onResult(false, "Failed to save image URL: ${e.message}", null)
                    }
            }
        }
        .addOnFailureListener { e ->
            onResult(false, "Failed to upload image: ${e.message}", null)
        }
}