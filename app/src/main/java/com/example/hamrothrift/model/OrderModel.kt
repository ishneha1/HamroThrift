package com.example.hamrothrift.model

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val totalPrice: Double = 0.0,
    val orderDate: Long = 0L,
    val status: String = "Pending",
    val buyerName: String ="",
    val itemName: String ="",
    val price: Double =0.0
)