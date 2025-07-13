// UploadRepository.kt
package com.example.hamrothrift.repository

import android.content.Context
import android.net.Uri
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.model.ProductUploadRequest
import kotlinx.coroutines.flow.Flow

interface UploadRepository {
    suspend fun uploadProduct(
        context: Context,
        uploadRequest: ProductUploadRequest
    ): Flow<UploadResult>

    suspend fun uploadImage(uri: Uri): Flow<String> // Returns image URL
    suspend fun saveProductToDatabase(product: ProductModel): Flow<Boolean>
}

sealed class UploadResult {
    object Loading : UploadResult()
    data class Success(val product: ProductModel) : UploadResult()
    data class Error(val message: String) : UploadResult()
}