package com.example.hamrothrift.repository

import com.example.hamrothrift.model.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class OrderRepositoryImpl : OrderRepository {
    private val database = FirebaseDatabase.getInstance()
    private val ordersRef = database.reference.child("orders")

    override suspend fun getAllOrders(): Flow<List<Order>> = callbackFlow {
        val listener = ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<Order>()
                for (childSnapshot in snapshot.children) {
                    val order = childSnapshot.getValue(Order::class.java)
                    order?.let { orders.add(it.copy(orderId = childSnapshot.key ?: "")) }
                }
                trySend(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { ordersRef.removeEventListener(listener) }
    }

    override suspend fun getOrderById(orderId: String): Order? {
        return try {
            val snapshot = ordersRef.child(orderId).get().await()
            snapshot.getValue(Order::class.java)?.copy(orderId = orderId)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addOrder(order: Order): Boolean {
        return try {
            val orderRef = ordersRef.push()
            val orderWithId = order.copy(orderId = orderRef.key ?: "")
            orderRef.setValue(orderWithId).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateOrder(order: Order): Boolean {
        return try {
            ordersRef.child(order.orderId).setValue(order).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteOrder(orderId: String): Boolean {
        return try {
            ordersRef.child(orderId).removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getUserOrders(userId: String): Flow<List<Order>> = callbackFlow {
        val listener = ordersRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orders = mutableListOf<Order>()
                    for (childSnapshot in snapshot.children) {
                        val order = childSnapshot.getValue(Order::class.java)
                        order?.let { orders.add(it.copy(orderId = childSnapshot.key ?: "")) }
                    }
                    trySend(orders)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { ordersRef.removeEventListener(listener) }
    }
}