package com.example.hamrothrift.repository

import android.content.Context
import android.net.Uri
import com.example.hamrothrift.model.UserModel
import com.google.firebase.auth.FirebaseUser


interface UserRepo {
    fun login(email : String,
              password : String,callback:(Boolean, String)-> Unit)


    fun register(email : String,
                 password : String,callback:(Boolean, String, String)-> Unit
    )


    fun forgetPassword(email: String,
                       callback: (Boolean, String) -> Unit)


    fun addUserToDatabase(userId : String, model: UserModel,
                          callback: (Boolean, String) -> Unit)


    fun deleteAccount(userId: String,
                      callback: (Boolean, String) -> Unit)

    fun editProfile(userId: String,data: MutableMap<String, Any>,
                    callback: (Boolean, String) -> Unit)

    fun getCurrentUser() : FirebaseUser?

    fun getUserById(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    )

    fun logout(callback: (Boolean, String) -> Unit)

    fun registerWithGoogle(
        idToken: String,
        callback: (Boolean, String, String) -> Unit
    )

    fun uploadImage(context: Context,imageUri: Uri, callback: (String?) -> Unit)

    fun getFileNameFromUri(context: Context,uri: Uri): String?

    fun updateUserProfile(
        userId: String,
        userModel: UserModel,
        onResult: (Boolean, String) -> Unit
    )

    fun getCurrentUserProfile(
        userId: String,
        onResult: (UserModel?, String) -> Unit
    )

    fun updateProfileImage(userId: String, imageUrl: String, callback: (Boolean, String) -> Unit)
}