package com.example.hamrothrift.model

class ProductModel(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val sellerId: String = "",
    val category: String = "",
    val isOnSale: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val originalPrice: Double? = null,
    val discount: Double? = null

)
