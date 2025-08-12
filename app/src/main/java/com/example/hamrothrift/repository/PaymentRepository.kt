package com.example.hamrothrift.repository

import com.example.hamrothrift.model.CartItem
import com.example.hamrothrift.model.PaymentResult
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    suspend fun processPayment(items: List<CartItem>, totalAmount: Double, paymentMethod: String): Flow<PaymentResult>
}