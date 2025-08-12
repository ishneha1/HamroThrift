package com.example.hamrothrift.repository

import android.content.Context
import android.net.Uri
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.model.ProductUploadRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class UploadRepositoryImpl : UploadRepository {

    private val database = FirebaseDatabase.getInstance()
    private val productsRef = database.reference.child("products")
    private val auth = FirebaseAuth.getInstance()

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dbmwufxbn",
            "api_key" to "511933986824672",
            "api_secret" to "C7WUm7KiQWZl7XaR9guDFTW3wU0"
        )
    )

    override suspend fun uploadProduct(
        context: Context,
        uploadRequest: ProductUploadRequest
    ): Flow<UploadResult> = flow {
        try {
            emit(UploadResult.Loading)

            val currentUserId = auth.currentUser?.uid
                ?: throw Exception("User not authenticated")

            // Upload image to Cloudinary if provided
            val imageUrl = if (uploadRequest.imageUri != null) {
                uploadImageToCloudinary(context, uploadRequest.imageUri)
            } else {
                ""
            }

            // Generate product ID
            val productId = productsRef.push().key ?: throw Exception("Failed to generate product ID")

            // Create product with the generated ID and sellerId
            val product = ProductModel(
                id = productId,
                name = uploadRequest.name,
                category = uploadRequest.category,
                price = uploadRequest.price.toDouble(),
                condition = uploadRequest.condition,
                description = uploadRequest.description,
                imageUrl = imageUrl,
                sellerId = currentUserId,
                isOnSale = uploadRequest.isOnSale,
                originalPrice = uploadRequest.originalPrice,
                discount = uploadRequest.discount,
                timestamp = System.currentTimeMillis()
            )

            // Save the product to Realtime Database
            productsRef.child(productId).setValue(product).await()

            emit(UploadResult.Success(product))

        } catch (e: Exception) {
            emit(UploadResult.Error(e.message ?: "Upload failed"))
        }
    }

    override suspend fun uploadImage(uri: Uri): Flow<String> = flow {
        throw UnsupportedOperationException("Use uploadProduct method instead")
    }

    override suspend fun saveProductToDatabase(product: ProductModel): Flow<Boolean> = flow {
        try {
            productsRef.child(product.id).setValue(product).await()
            emit(true)
        } catch (e: Exception) {
            throw Exception("Failed to save product to database: ${e.message}")
        }
    }

    private suspend fun uploadImageToCloudinary(context: Context, uri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw Exception("Cannot open image file")

                val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(file)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                val uploadResult = cloudinary.uploader().upload(
                    file.absolutePath,
                    ObjectUtils.asMap(
                        "folder", "hamrothrift/products",
                        "resource_type", "image",
                        "quality", "auto",
                        "fetch_format", "auto"
                    )
                )

                file.delete()

                val imageUrl = uploadResult["secure_url"] as? String
                    ?: throw Exception("Failed to get image URL from Cloudinary response")

                imageUrl

            } catch (e: Exception) {
                throw Exception("Failed to upload image to Cloudinary: ${e.message}")
            }
        }
    }
}