package com.example.hamrothrift.repository

import com.example.hamrothrift.model.SalesOverview
import com.example.hamrothrift.model.SalesData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

interface SalesRepository {
    suspend fun getSalesOverview(timeRange: String): Result<SalesOverview>
}

class SalesRepositoryImpl : SalesRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override suspend fun getSalesOverview(timeRange: String): Result<SalesOverview> {
        return try {
            val days = when (timeRange) {
                "7 Days" -> 7
                "30 Days" -> 30
                "90 Days" -> 90
                else -> 7
            }

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -days)
            val fromDate = calendar.time

            val documents = firestore.collection("orders")
                .whereGreaterThanOrEqualTo("orderDate", fromDate)
                .get()
                .await()

            val dailyDataMap = mutableMapOf<String, SalesData>()
            var totalSells = 0
            var totalBuys = 0
            var totalSellAmount = 0.0
            var totalBuyAmount = 0.0

            for (doc in documents) {
                val status = doc.getString("status") ?: continue
                val totalPrice = doc.getDouble("totalPrice") ?: 0.0
                val orderDate = doc.getDate("orderDate") ?: continue
                val dateStr = dateFormat.format(orderDate)

                val currentData = dailyDataMap.getOrDefault(
                    dateStr,
                    SalesData(date = dateStr)
                )

                if (status == "Delivered" || status == "Completed") {
                    dailyDataMap[dateStr] = currentData.copy(
                        sellCount = currentData.sellCount + 1,
                        sellAmount = currentData.sellAmount + totalPrice
                    )
                    totalSells++
                    totalSellAmount += totalPrice
                } else {
                    dailyDataMap[dateStr] = currentData.copy(
                        buyCount = currentData.buyCount + 1,
                        buyAmount = currentData.buyAmount + totalPrice
                    )
                    totalBuys++
                    totalBuyAmount += totalPrice
                }
            }

            val salesOverview = SalesOverview(
                totalSells = totalSells,
                totalBuys = totalBuys,
                totalSellAmount = totalSellAmount,
                totalBuyAmount = totalBuyAmount,
                dailyData = dailyDataMap.values.sortedBy { it.date }
            )

            Result.success(salesOverview)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}