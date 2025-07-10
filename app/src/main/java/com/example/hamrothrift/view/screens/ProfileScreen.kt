package com.example.hamrothrift.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(navController: NavController,
                  viewModel: UserViewModel = viewModel())
{
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    var userName by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    // Use ViewModel to get user data instead of direct Firestore call
    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            viewModel.getUserById(uid)
        }
    }

    // Observe user data from ViewModel
    viewModel.users.observeAsState().value?.let { userModel ->
        userName = "${userModel.firstName} ${userModel.lastName}"
        profileImageUrl = userModel.userImage
    }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (profileImageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(profileImageUrl),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Default Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                tint = text
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = userName,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = text
        )
        Text(
            text = user?.email ?: "",
            fontSize = 16.sp,
            color = text.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { navController.navigate("edit_profile") },
            colors = ButtonDefaults.buttonColors(containerColor = buttton),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Edit Profile")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Divider(color = text.copy(alpha = 0.2f))

        ProfileOption("My Orders", Icons.Default.ShoppingCart) { navController.navigate("my_orders") }
        ProfileOption("All Orders", Icons.Default.List) { navController.navigate("all_orders") }
        ProfileOption("Billing Address", Icons.Default.LocationOn) { navController.navigate("billing_address") }
        ProfileOption("Change Password", Icons.Default.Lock) { navController.navigate("change_password") }
        ProfileOption("Logout", Icons.Default.ExitToApp) {
            viewModel.logout { success, message ->
                if (success) {
                    // Navigate to login screen
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileOption(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = buttton,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
            color = text
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Arrow",
            tint = text.copy(alpha = 0.7f)
        )
    }
}