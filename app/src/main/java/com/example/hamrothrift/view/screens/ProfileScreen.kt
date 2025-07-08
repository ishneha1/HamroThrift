package com.example.hamrothrift.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun ProfileScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    var userName by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { document ->
                userName = document.getString("name") ?: ""
                profileImageUrl = document.getString("profileImage")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

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
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = userName, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(text = user?.email ?: "", fontSize = 16.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { navController.navigate("edit_profile") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA4CCD9))
        ) {
            Text("Edit Profile")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Divider()
        ProfileOption("My Orders", Icons.Default.ShoppingCart) { navController.navigate("my_orders") }
        ProfileOption("All Orders", Icons.Default.List) { navController.navigate("all_orders") }
        ProfileOption("Billing Address", Icons.Default.LocationOn) { navController.navigate("billing_address") }
        ProfileOption("Change Password", Icons.Default.Lock) { navController.navigate("change_password") }
    }
}

@Composable
fun ProfileOption(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
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
            tint = Color(0xFFA4CCD9),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Arrow",
            tint = Color.Gray
        )
    }
}