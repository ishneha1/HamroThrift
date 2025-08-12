package com.example.hamrothrift.model

sealed class PaymentResult {
    object Loading : PaymentResult()
    data class Success(val transactionId: String) : PaymentResult()
    data class Error(val message: String) : PaymentResult()
}