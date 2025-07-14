package com.example.hamrothrift.repository

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
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
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
    private val db = Firebase.firestore

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
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Account Created", "${auth.currentUser?.uid}")
                } else {
                    callback(false, "${it.exception?.message}", "")
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
        ref.child(userId).setValue(model)
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
                            userImage = it.photoUrl?.toString() ?: ""
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
    override fun updateUserProfile(
        userId: String,
        userModel: UserModel,
        onResult: (Boolean, String) -> Unit
    ) {
        val userData = hashMapOf(
            "firstName" to userModel.firstName,
            "lastName" to userModel.lastName,
            "gender" to userModel.gender,
            "updatedAt" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(userId)
            .update(userData as Map<String, Any>)
            .addOnSuccessListener {
                onResult(true, "Profile updated successfully!")
            }
            .addOnFailureListener { e ->
                onResult(false, "Failed to update profile: ${e.message}")
            }
    }

    override fun getCurrentUserProfile(
        userId: String,
        onResult: (UserModel?, String) -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = UserModel(
                        userId = userId,
                        firstName = document.getString("firstName") ?: "",
                        lastName = document.getString("lastName") ?: "",
                        gender = document.getString("gender") ?: "",
                        email = document.getString("email") ?: "",
                        password = "", // Don't expose password
                    )
                    onResult(user, "Profile loaded successfully")
                } else {
                    onResult(null, "User profile not found")
                }
            }
            .addOnFailureListener { e ->
                onResult(null, "Failed to load profile: ${e.message}")
            }
    }



}