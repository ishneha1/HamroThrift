package com.example.hamrothrift.repository

import android.content.Context
import com.example.hamrothrift.model.CartItem
import com.example.hamrothrift.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CartRepositoryImpl(private val context: Context) : CartRepository {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override suspend fun addToCart(product: ProductModel, quantity: Int): Flow<Boolean> = callbackFlow {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val cartRef = database.reference.child("carts").child(userId).child("items")

            // Check if product already exists in cart
            val existingSnapshot = cartRef.orderByChild("product/id").equalTo(product.id).get().await()

            if (existingSnapshot.children.count() == 0) {
                // Add new item
                val cartItem = CartItem(
                    product = product,
                    quantity = quantity,
                    addedAt = System.currentTimeMillis()
                )
                val newItemRef = cartRef.push()
                newItemRef.setValue(cartItem).await()
            } else {
                // Update existing item quantity
                val existingItem = existingSnapshot.children.first()
                val currentQuantity = existingItem.child("quantity").getValue(Int::class.java) ?: 0
                existingItem.ref.child("quantity").setValue(currentQuantity + quantity).await()
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

        val cartRef = database.reference.child("carts").child(userId).child("items")
        val listener = cartRef.orderByChild("addedAt").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<CartItem>()
                for (childSnapshot in snapshot.children) {
                    val cartItem = childSnapshot.getValue(CartItem::class.java)
                    cartItem?.let {
                        items.add(0, it.copy(id = childSnapshot.key ?: ""))
                    }
                }
                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose { cartRef.removeEventListener(listener) }
    }

    override suspend fun updateQuantity(itemId: String, newQuantity: Int): Flow<Boolean> = callbackFlow {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            database.reference.child("carts").child(userId).child("items")
                .child(itemId).child("quantity").setValue(newQuantity).await()
            trySend(true)
        } catch (e: Exception) {
            trySend(false)
        }
        awaitClose()
    }

    override suspend fun removeItem(itemId: String): Flow<Boolean> = callbackFlow {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            database.reference.child("carts").child(userId).child("items")
                .child(itemId).removeValue().await()
            trySend(true)
        } catch (e: Exception) {
            trySend(false)
        }
        awaitClose()
    }

    override suspend fun clearCart(): Flow<Boolean> = callbackFlow {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            database.reference.child("carts").child(userId).child("items").removeValue().await()
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

        val cartRef = database.reference.child("carts").child(userId).child("items")
        val listener = cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalCount = 0
                for (childSnapshot in snapshot.children) {
                    val quantity = childSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                    totalCount += quantity
                }
                trySend(totalCount)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(0)
            }
        })

        awaitClose { cartRef.removeEventListener(listener) }
    }
}