// UploadRepository.kt
package com.example.hamrothrift.repository

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface UploadRepository {
    suspend fun uploadProduct(
        context: Context,
        imageUri: Uri,
        name: String,
        category: String,
        price: String,
        condition: String,
        description: String
    ): Flow<UploadResult>
}

sealed class UploadResult {
    object Loading : UploadResult()
    object Success : UploadResult()
    data class Error(val message: String) : UploadResult()
}