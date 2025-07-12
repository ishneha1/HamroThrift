package com.example.hamrothrift.model

data class BillingAddress(
    val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = "Nepal"
)