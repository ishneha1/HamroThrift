package com.example.hamrothrift.repository

import com.example.hamrothrift.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun getAllOrders(): Flow<List<Order>>
    suspend fun getOrderById(orderId: String): Order?
    suspend fun addOrder(order: Order): Boolean
    suspend fun updateOrder(order: Order): Boolean
    suspend fun deleteOrder(orderId: String): Boolean
    suspend fun getUserOrders(userId: String): Flow<List<Order>>
}