package com.example.hamrothrift.model

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val shippingAddress: String = "",
    val paymentMethod: String = "",
    val orderDate: Long = System.currentTimeMillis(),
    val deliveryDate: Long? = null,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val transactionId: String = ""
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    PAID,  // Add this status for payment
    SHIPPED,
    DELIVERED,
    CANCELLED
}