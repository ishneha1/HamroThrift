// app/src/main/java/com/example/hamrothrift/view/screens/ProfileScreen.kt
package com.example.hamrothrift.view.screens

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.hamrothrift.view.EditProfileActivity
import com.example.hamrothrift.view.HomepageActivity
import com.example.hamrothrift.view.theme.ui.theme.*
import com.example.hamrothrift.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(viewModel: UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    var userName by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    // Dialog state
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            viewModel.getUserById(uid)
        }
    }

    viewModel.users.observeAsState().value?.let { userModel ->
        userName = "${userModel.firstName} ${userModel.lastName}"
        profileImageUrl = userModel.profileImageUrl
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
            onClick = {
                context.startActivity(Intent(context, EditProfileActivity::class.java))
            },
            colors = ButtonDefaults.buttonColors(containerColor = buttton),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Edit Profile")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Divider(color = text.copy(alpha = 0.2f))

        ProfileOption("My Orders", Icons.Default.ShoppingCart) {
            val intent = Intent(context, MyOrdersActivity::class.java)
            context.startActivity(intent)
        }
        ProfileOption("All Orders", Icons.Default.List) {
            val intent = Intent(context, AllOrdersActivity::class.java)
            context.startActivity(intent)
        }
        ProfileOption("Billing Address", Icons.Default.LocationOn) {
            val intent = Intent(context, BillingAddressActivity::class.java)
            context.startActivity(intent)
        }
        ProfileOption("Change Password", Icons.Default.Lock) {
            val intent = Intent(context, ChangePasswordActivity::class.java)
            context.startActivity(intent)
        }
        ProfileOption("Logout", Icons.Default.ExitToApp) {
            showLogoutDialog = true
        }
        ProfileOption("Delete Account", Icons.Default.Delete) {
            showDeleteDialog = true
        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout { success, _ ->
                        if (success) {
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(context, HomepageActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
                }) { Text("Logout") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Delete account confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    user?.uid?.let { uid ->
                        viewModel.deleteAccount(uid) { success, _ ->
                            if (success) {
                                FirebaseAuth.getInstance().signOut()
                                val intent = Intent(context, HomepageActivity::class.java)
                                context.startActivity(intent)
                            }
                        }
                    }
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
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