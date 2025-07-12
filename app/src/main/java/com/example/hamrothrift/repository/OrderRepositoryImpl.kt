package com.example.hamrothrift.repository

import com.example.hamrothrift.model.Order
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class OrderRepositoryImpl : OrderRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val ordersCollection = firestore.collection("orders")

    override suspend fun getAllOrders(): Flow<List<Order>> = callbackFlow {
        val snapshotListener = ordersCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val orders = snapshot.documents.mapNotNull { document ->
                    document.toObject(Order::class.java)?.copy(orderId = document.id)
                }
                trySend(orders)
            }
        }
        awaitClose { snapshotListener.remove() }
    }

    override suspend fun getOrderById(orderId: String): Order? {
        return try {
            val document = ordersCollection.document(orderId).get().await()
            document.toObject(Order::class.java)?.copy(orderId = document.id)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addOrder(order: Order): Boolean {
        return try {
            ordersCollection.add(order).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateOrder(order: Order): Boolean {
        return try {
            ordersCollection.document(order.orderId).set(order).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteOrder(orderId: String): Boolean {
        return try {
            ordersCollection.document(orderId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getUserOrders(userId: String): Flow<List<Order>> = callbackFlow {
        val snapshotListener = ordersCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val orders = snapshot.documents.mapNotNull { document ->
                        document.toObject(Order::class.java)?.copy(orderId = document.id)
                    }
                    trySend(orders)
                }
            }
        awaitClose { snapshotListener.remove() }
    }
}