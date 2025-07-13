package com.example.hamrothrift.model

data class SalesData(
    val date: String = "",
    val sellCount: Int = 0,
    val buyCount: Int = 0,
    val sellAmount: Double = 0.0,
    val buyAmount: Double = 0.0
)

data class SalesOverview(
    val totalSells: Int = 0,
    val totalBuys: Int = 0,
    val totalSellAmount: Double = 0.0,
    val totalBuyAmount: Double = 0.0,
    val dailyData: List<SalesData> = emptyList()
)