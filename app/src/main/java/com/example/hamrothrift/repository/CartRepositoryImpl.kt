package com.example.hamrothrift.repository

import android.content.Context
import android.util.Log
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

class CartRepositoryImpl(private val context: Context? = null) : CartRepository {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private suspend fun getCurrentUserId(): String? {
        return try {
            // Check if user is already authenticated
            val currentUser = auth.currentUser
            if (currentUser != null) {
                Log.d("CartRepository", "User already authenticated: ${currentUser.uid}")
                return currentUser.uid
            }

            // If not authenticated, sign in anonymously
            Log.d("CartRepository", "Signing in anonymously...")
            val result = auth.signInAnonymously().await()
            val userId = result.user?.uid
            Log.d("CartRepository", "Anonymous authentication successful: $userId")
            userId
        } catch (e: Exception) {
            Log.e("CartRepository", "Authentication failed: ${e.message}", e)
            null
        }
    }

    override suspend fun addToCart(product: ProductModel, quantity: Int): Flow<Boolean> = callbackFlow {
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e("CartRepository", "Failed to authenticate user")
                trySend(false)
                close()
                return@callbackFlow
            }

            Log.d("CartRepository", "Adding product to cart: ${product.name}")

            val cartRef = database.reference.child("carts").child(userId).child("items")

            // Check if product already exists in cart
            val existingSnapshot = cartRef.orderByChild("product/id").equalTo(product.id).get().await()

            if (existingSnapshot.children.count() == 0) {
                // Add new item
                val cartItem = CartItem(
                    id = "",
                    product = product,
                    quantity = quantity,
                    addedAt = System.currentTimeMillis()
                )
                val newItemRef = cartRef.push()
                newItemRef.setValue(cartItem).await()
                Log.d("CartRepository", "New item added to cart successfully")
            } else {
                // Update existing item quantity
                val existingItem = existingSnapshot.children.first()
                val currentQuantity = existingItem.child("quantity").getValue(Int::class.java) ?: 0
                existingItem.ref.child("quantity").setValue(currentQuantity + quantity).await()
                Log.d("CartRepository", "Updated existing item quantity successfully")
            }

            trySend(true)
            close()
        } catch (e: Exception) {
            Log.e("CartRepository", "Error adding to cart: ${e.message}", e)
            trySend(false)
            close()
        }
    }

    override suspend fun getCartItems(): Flow<List<CartItem>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            Log.e("CartRepository", "Failed to authenticate user for getCartItems")
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        Log.d("CartRepository", "Loading cart items for user: $userId")

        val cartRef = database.reference.child("carts").child(userId).child("items")
        val listener = cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<CartItem>()
                for (childSnapshot in snapshot.children) {
                    try {
                        val cartItem = childSnapshot.getValue(CartItem::class.java)
                        cartItem?.let {
                            items.add(it.copy(id = childSnapshot.key ?: ""))
                        }
                    } catch (e: Exception) {
                        Log.e("CartRepository", "Error parsing cart item: ${e.message}")
                    }
                }
                items.sortByDescending { it.addedAt }
                Log.d("CartRepository", "Loaded ${items.size} cart items")
                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CartRepository", "Error loading cart: ${error.message}")
                trySend(emptyList())
            }
        })

        awaitClose { cartRef.removeEventListener(listener) }
    }

    override suspend fun updateQuantity(itemId: String, newQuantity: Int): Flow<Boolean> = callbackFlow {
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e("CartRepository", "Failed to authenticate user for updateQuantity")
                trySend(false)
                close()
                return@callbackFlow
            }

            Log.d("CartRepository", "Updating quantity for item: $itemId to $newQuantity")

            if (newQuantity <= 0) {
                database.reference.child("carts").child(userId).child("items")
                    .child(itemId).removeValue().await()
                Log.d("CartRepository", "Item removed due to zero quantity")
            } else {
                database.reference.child("carts").child(userId).child("items")
                    .child(itemId).child("quantity").setValue(newQuantity).await()
                Log.d("CartRepository", "Updated quantity to $newQuantity")
            }
            trySend(true)
            close()
        } catch (e: Exception) {
            Log.e("CartRepository", "Error updating quantity: ${e.message}", e)
            trySend(false)
            close()
        }
    }

    override suspend fun removeItem(itemId: String): Flow<Boolean> = callbackFlow {
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e("CartRepository", "Failed to authenticate user for removeItem")
                trySend(false)
                close()
                return@callbackFlow
            }

            Log.d("CartRepository", "Removing item: $itemId")

            database.reference.child("carts").child(userId).child("items")
                .child(itemId).removeValue().await()
            Log.d("CartRepository", "Item removed from cart successfully")
            trySend(true)
            close()
        } catch (e: Exception) {
            Log.e("CartRepository", "Error removing item: ${e.message}", e)
            trySend(false)
            close()
        }
    }

    override suspend fun clearCart(): Flow<Boolean> = callbackFlow {
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                Log.e("CartRepository", "Failed to authenticate user for clearCart")
                trySend(false)
                close()
                return@callbackFlow
            }

            Log.d("CartRepository", "Clearing cart")

            database.reference.child("carts").child(userId).child("items").removeValue().await()
            Log.d("CartRepository", "Cart cleared successfully")
            trySend(true)
            close()
        } catch (e: Exception) {
            Log.e("CartRepository", "Error clearing cart: ${e.message}", e)
            trySend(false)
            close()
        }
    }

    override suspend fun getCartItemCount(): Flow<Int> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            Log.e("CartRepository", "Failed to authenticate user for getCartItemCount")
            trySend(0)
            close()
            return@callbackFlow
        }

        Log.d("CartRepository", "Getting cart item count")

        val cartRef = database.reference.child("carts").child(userId).child("items")
        val listener = cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalCount = 0
                for (childSnapshot in snapshot.children) {
                    val quantity = childSnapshot.child("quantity").getValue(Int::class.java) ?: 0
                    totalCount += quantity
                }
                Log.d("CartRepository", "Total cart count: $totalCount")
                trySend(totalCount)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CartRepository", "Error getting cart count: ${error.message}")
                trySend(0)
            }
        })

        awaitClose { cartRef.removeEventListener(listener) }
    }
}