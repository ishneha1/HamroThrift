package com.example.hamrothrift.repository

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import com.example.hamrothrift.R
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.hamrothrift.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.InputStream
import java.util.concurrent.Executors

class UserRepoImpl : UserRepo {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    val ref: DatabaseReference = database.reference.child("users")

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dbmwufxbn",
            "api_key" to "511933986824672",
            "api_secret" to "C7WUm7KiQWZl7XaR9guDFTW3wU0"
        )
    )


    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Login Suceesfully")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        // First check if email exists (can receive emails)
        checkIfEmailExists(email) { emailExists ->
            if (emailExists) {
                // Email exists, now check if it's already registered in our app
                auth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val signInMethods = task.result?.signInMethods ?: emptyList()
                            if (signInMethods.isNotEmpty()) {
                                // Email already registered in our app
                                callback(false, "This email is already registered. Please try logging in instead.", "")
                            } else {
                                // Email exists but not registered, proceed with registration
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { registerTask ->
                                        if (registerTask.isSuccessful) {
                                            callback(true, "Account Created Successfully", "${auth.currentUser?.uid}")
                                        } else {
                                            callback(false, "Registration failed: ${registerTask.exception?.message}", "")
                                        }
                                    }
                            }
                        } else {
                            callback(false, "Failed to verify email: ${task.exception?.message}", "")
                        }
                    }
            } else {
                // Email doesn't exist
                callback(false, "This email address does not exist. Please use a valid email address.", "")
            }
        }
    }

    private fun checkIfEmailExists(email: String, callback: (Boolean) -> Unit) {
        // Create a temporary Firebase Auth instance for testing
        val tempAuth = FirebaseAuth.getInstance()

        // Try to send password reset email - if email exists, it will succeed
        tempAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Email exists and can receive emails
                    callback(true)
                } else {
                    // Check the specific error
                    val errorMessage = task.exception?.message?.lowercase() ?: ""
                    when {
                        errorMessage.contains("user-not-found") ||
                                errorMessage.contains("invalid-email") ||
                                errorMessage.contains("user not found") ||
                                errorMessage.contains("no user record") -> {
                            // Email doesn't exist
                            callback(false)
                        }
                        else -> {
                            // Other errors (network, etc.) - assume email exists
                            callback(true)
                        }
                    }
                }
            }
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Password Reset link sent to $email")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        // Set default profile image if none provided
        val userWithDefaultImage = if (model.profileImageUrl.isEmpty()) {
            model.copy(
                profileImageUrl = "android.resource://com.example.hamrothrift/${R.drawable.profilephoto}"
            )
        } else {
            model
        }

        ref.child(userId).setValue(userWithDefaultImage)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "user added")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }
    override fun deleteAccount(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "user deleted")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun editProfile(
        userId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).updateChildren(data)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Profile Edited")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }


    override fun getUserById(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        ref.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var users = snapshot.getValue(UserModel::class.java)
                    if (users != null) {
                        callback(true, "data fetched", users)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try {
            auth.signOut()
            callback(true, "Logout Succesfull")
        } catch (e: Exception) {
            callback(false, e.message.toString())
        }

    }

    override fun registerWithGoogle(
        idToken: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userModel = UserModel(
                            userId = it.uid,
                            email = it.email ?: "",
                            firstName = it.displayName ?: "",
                            profileImageUrl = it.photoUrl?.toString() ?: ""
                        )
                        database.getReference("users").child(it.uid).setValue(userModel)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    callback(true, "Google Sign-in successful", it.uid)
                                } else {
                                    callback(false, "Failed to save user data", "")
                                }
                            }
                    }
                } else {
                    callback(false, "Google Sign-in failed", "")
                }
            }

    }

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri)


                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )

                var imageUrl = response["url"] as String?

                imageUrl = imageUrl?.replace("http://", "https://")


                Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

    override fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    // Fixed: Use Realtime Database instead of Firestore
    override fun updateUserProfile(
        userId: String,
        userModel: UserModel,
        onResult: (Boolean, String) -> Unit
    ) {
        val userData = hashMapOf(
            "firstName" to userModel.firstName,
            "lastName" to userModel.lastName,
            "gender" to userModel.gender,
            "userImage" to userModel.profileImageUrl,
            "updatedAt" to System.currentTimeMillis()
        )

        ref.child(userId).updateChildren(userData as Map<String, Any>)
            .addOnSuccessListener {
                onResult(true, "Profile updated successfully!")
            }
            .addOnFailureListener { e ->
                onResult(false, "Failed to update profile: ${e.message}")
            }
    }

    // Fixed: Use Realtime Database instead of Firestore
    override fun getCurrentUserProfile(
        userId: String,
        onResult: (UserModel?, String) -> Unit
    ) {
        ref.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    onResult(user, "Profile loaded successfully")
                } else {
                    onResult(null, "User profile not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(null, "Failed to load profile: ${error.message}")
            }
        })
    }
    // In UserRepoImpl.kt, add this method:
    override fun updateProfileImage(userId: String, imageUrl: String, callback: (Boolean, String) -> Unit) {
        ref.child(userId).child("profileImageUrl").setValue(imageUrl)
            .addOnSuccessListener {
                callback(true, "Profile image updated successfully!")
            }
            .addOnFailureListener { e ->
                callback(false, "Failed to update profile image: ${e.message}")
            }
    }


}