package com.example.hamrothrift.repository

import com.example.hamrothrift.model.SalesOverview
import com.example.hamrothrift.model.SalesData
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

interface SalesRepository {
    suspend fun getSalesOverview(timeRange: String): Result<SalesOverview>
}

class SalesRepositoryImpl : SalesRepository {
    private val database = FirebaseDatabase.getInstance()
    private val ordersRef = database.reference.child("orders")
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
            val fromTimestamp = calendar.timeInMillis

            val snapshot = ordersRef.orderByChild("orderDate").startAt(fromTimestamp.toDouble()).get().await()

            val dailyDataMap = mutableMapOf<String, SalesData>()
            var totalSells = 0
            var totalBuys = 0
            var totalSellAmount = 0.0
            var totalBuyAmount = 0.0

            for (childSnapshot in snapshot.children) {
                val status = childSnapshot.child("status").getValue(String::class.java) ?: continue
                val totalPrice = childSnapshot.child("totalPrice").getValue(Double::class.java) ?: 0.0
                val orderTimestamp = childSnapshot.child("orderDate").getValue(Long::class.java) ?: continue
                val orderDate = Date(orderTimestamp)
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