package com.example.hamrothrift.model

data class Order(
    val orderId: String = "",
    val buyerName: String = "",
    val itemName: String = "",
    val price: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)