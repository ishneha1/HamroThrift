package com.example.hamrothrift.repository

import android.content.Context
import android.net.Uri
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.model.ProductUploadRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UploadRepositoryImpl : UploadRepository {

    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override suspend fun uploadProduct(
        context: Context,
        uploadRequest: ProductUploadRequest
    ): Flow<UploadResult> = flow {
        try {
            emit(UploadResult.Loading)

            // Step 1: Upload image
            val imageUrl = uploadImageToFirebase(uploadRequest.imageUri)

            // Step 2: Create product object with correct constructor
            val product = ProductModel(
                id = UUID.randomUUID().toString(),
                name = uploadRequest.name,
                category = uploadRequest.category,
                price = uploadRequest.price.toDoubleOrNull() ?: 0.0,
                description = uploadRequest.description,
                imageUrl = imageUrl,
                sellerId = getCurrentUserId()
            )

            // Step 3: Save to database
            val success = saveProductToFirestore(product)

            if (success) {
                emit(UploadResult.Success(product))
            } else {
                emit(UploadResult.Error("Failed to save product to database"))
            }

        } catch (e: Exception) {
            emit(UploadResult.Error(e.message ?: "Upload failed"))
        }
    }

    override suspend fun uploadImage(uri: Uri): Flow<String> = flow {
        try {
            // Create a unique filename for the image
            val fileName = "product_${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference
                .child("products")
                .child("images")
                .child(fileName)

            // Upload the file
            val uploadTask = storageRef.putFile(uri).await()

            // Get the download URL
            val downloadUrl = storageRef.downloadUrl.await()

            emit(downloadUrl.toString())

        } catch (e: Exception) {
            throw Exception("Failed to upload image: ${e.message}")
        }
    }

    override suspend fun saveProductToDatabase(product: ProductModel): Flow<Boolean> = flow {
        try {
            // Save product to Firestore
            firestore.collection("products")
                .document(product.id)
                .set(product)
                .await()

            emit(true)
        } catch (e: Exception) {
            throw Exception("Failed to save product to database: ${e.message}")
        }
    }

    private suspend fun uploadImageToFirebase(uri: Uri): String {
        try {
            val fileName = "product_${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference
                .child("products")
                .child("images")
                .child(fileName)

            // Upload the file
            storageRef.putFile(uri).await()

            // Get and return the download URL
            return storageRef.downloadUrl.await().toString()

        } catch (e: Exception) {
            throw Exception("Failed to upload image to Firebase Storage: ${e.message}")
        }
    }

    private suspend fun saveProductToFirestore(product: ProductModel): Boolean {
        return try {
            firestore.collection("products")
                .document(product.id)
                .set(product)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: "anonymous_${System.currentTimeMillis()}"
    }
}