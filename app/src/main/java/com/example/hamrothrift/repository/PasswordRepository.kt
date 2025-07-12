package com.example.hamrothrift.repository

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

interface PasswordRepository {
    suspend fun sendPasswordResetEmail(email: String): Result<Boolean>
    suspend fun reauthenticateUser(currentPassword: String): Result<Boolean>
    suspend fun updatePassword(newPassword: String): Result<Boolean>
}

class PasswordRepositoryImpl : PasswordRepository {
    private val auth = FirebaseAuth.getInstance()

    override suspend fun sendPasswordResetEmail(email: String): Result<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reauthenticateUser(currentPassword: String): Result<Boolean> {
        return try {
            val user = auth.currentUser
            val email = user?.email
            if (user != null && email != null) {
                val credential = EmailAuthProvider.getCredential(email, currentPassword)
                user.reauthenticate(credential).await()
                Result.success(true)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePassword(newPassword: String): Result<Boolean> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                user.updatePassword(newPassword).await()
                Result.success(true)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}