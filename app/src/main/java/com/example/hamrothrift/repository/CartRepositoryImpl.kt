package com.example.hamrothrift.repository

import android.content.Context
import com.example.hamrothrift.model.CartItem
import com.example.hamrothrift.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CartRepositoryImpl(private val context: Context) : CartRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override suspend fun addToCart(product: ProductModel, quantity: Int): Flow<Boolean> = callbackFlow {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            // Check if product already exists in cart
            val existingItems = firestore.collection("carts")
                .document(userId)
                .collection("items")
                .whereEqualTo("product.id", product.id)
                .get()
                .await()

            if (existingItems.isEmpty) {
                // Add new item
                val cartItem = CartItem(
                    product = product,
                    quantity = quantity
                )
                firestore.collection("carts")
                    .document(userId)
                    .collection("items")
                    .add(cartItem)
                    .await()
            } else {
                // Update existing item quantity
                val document = existingItems.documents[0]
                val currentQuantity = document.getLong("quantity")?.toInt() ?: 0
                document.reference.update("quantity", currentQuantity + quantity).await()
            }

            trySend(true)
        } catch (e: Exception) {
            trySend(false)
        }
        awaitClose()
    }

    override suspend fun getCartItems(): Flow<List<CartItem>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            awaitClose()
            return@callbackFlow
        }

        val listener = firestore.collection("carts")
            .document(userId)
            .collection("items")
            .orderBy("addedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { document ->
                    try {
                        document.toObject(CartItem::class.java)?.copy(id = document.id)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(items)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun updateQuantity(itemId: String, newQuantity: Int): Flow<Boolean> = callbackFlow {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            firestore.collection("carts")
                .document(userId)
                .collection("items")
                .document(itemId)
                .update("quantity", newQuantity)
                .await()

            trySend(true)
        } catch (e: Exception) {
            trySend(false)
        }
        awaitClose()
    }

    override suspend fun removeItem(itemId: String): Flow<Boolean> = callbackFlow {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            firestore.collection("carts")
                .document(userId)
                .collection("items")
                .document(itemId)
                .delete()
                .await()

            trySend(true)
        } catch (e: Exception) {
            trySend(false)
        }
        awaitClose()
    }

    override suspend fun clearCart(): Flow<Boolean> = callbackFlow {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            val batch = firestore.batch()
            val items = firestore.collection("carts")
                .document(userId)
                .collection("items")
                .get()
                .await()

            items.forEach { batch.delete(it.reference) }
            batch.commit().await()

            trySend(true)
        } catch (e: Exception) {
            trySend(false)
        }
        awaitClose()
    }

    override suspend fun getCartItemCount(): Flow<Int> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(0)
            awaitClose()
            return@callbackFlow
        }

        val listener = firestore.collection("carts")
            .document(userId)
            .collection("items")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(0)
                    return@addSnapshotListener
                }

                val count = snapshot?.documents?.sumOf {
                    it.getLong("quantity")?.toInt() ?: 0
                } ?: 0

                trySend(count)
            }

        awaitClose { listener.remove() }
    }
}