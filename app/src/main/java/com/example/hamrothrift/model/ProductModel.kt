package com.example.hamrothrift.model

data class ProductModel(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val sellerId: String = "",
    val category: String = "",
    val condition: String = "",
    val isOnSale: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val originalPrice: Double? = null,
    val discount: Double? = null

)

data class ProductUploadRequest(
    val name: String,
    val category: String,
    val price: String,
    val condition: String,
    val description: String,
    val imageUri: android.net.Uri
)
