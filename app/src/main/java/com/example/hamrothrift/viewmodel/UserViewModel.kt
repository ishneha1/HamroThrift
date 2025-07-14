package com.example.hamrothrift.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hamrothrift.model.UserModel
import com.example.hamrothrift.repository.UserRepo
import com.google.firebase.auth.FirebaseUser


class UserViewModel(val repo : UserRepo): ViewModel() {
    fun login(
        email: String,
        password: String, callback: (Boolean, String) -> Unit
    ) {
        repo.login(email, password, callback)
    }

    fun register(
        email: String,
        password: String, callback: (Boolean, String, String) -> Unit
    ) {
        repo.register(email, password, callback)
    }

    fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.forgetPassword(email, callback)
    }


    fun addUserToDatabase(
        userId: String, model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addUserToDatabase(userId, model, callback)
    }


    fun deleteAccount(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.deleteAccount(userId, callback)
    }

    fun editProfile(
        userId: String, data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        repo.editProfile(userId, data, callback)
    }

    fun getCurrentUser(): FirebaseUser? {
        return repo.getCurrentUser()
    }

    private val _users = MutableLiveData<UserModel?>()

    val users: LiveData<UserModel?> get() = _users

    fun getUserById(
        userId: String
    ) {
        repo.getUserById(userId) { sucess, message, data ->
            if (sucess) {
                _users.postValue(data)
            } else {
                _users.postValue(null)
            }
        }
    }

    fun logout(callback: (Boolean, String) -> Unit) {
        repo.logout(callback)
    }
    fun uploadImage(context: Context,imageUri: Uri, callback: (String?) -> Unit){
        repo.uploadImage(context,imageUri,callback)
    }

    fun updateUserProfile(
        userId: String,
        userModel: UserModel,
        onResult: (Boolean, String) -> Unit
    ) {
        repo.updateUserProfile(userId, userModel, onResult)
    }

    fun getCurrentUserProfile(
        userId: String,
        onResult: (UserModel?, String) -> Unit
    ) {
        repo.getCurrentUserProfile(userId, onResult)
    }

    fun validateProfileData(
        firstName: String,
        lastName: String,
        phone: String
    ): Boolean {
        return firstName.isNotBlank() &&
                lastName.isNotBlank()
    }

}

