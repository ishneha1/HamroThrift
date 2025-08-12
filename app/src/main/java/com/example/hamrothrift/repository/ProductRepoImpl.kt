package com.example.hamrothrift.repository

import com.example.hamrothrift.model.ProductModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductRepoImpl : ProductRepo {
    private val database = FirebaseDatabase.getInstance()
    private val productsRef: DatabaseReference = database.reference.child("products")

    override suspend fun getAllProducts(): Flow<List<ProductModel>> = callbackFlow {
        val snapshotListener = database.reference.child("products")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val products = mutableListOf<ProductModel>()
                    for (childSnapshot in snapshot.children) {
                        try {
                            val product = childSnapshot.getValue(ProductModel::class.java)
                            product?.let {
                                products.add(it.copy(id = childSnapshot.key ?: ""))
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("ProductRepo", "Error parsing product", e)
                        }
                    }
                    trySend(products)
                }

                override fun onCancelled(error: DatabaseError) {
                    android.util.Log.e("ProductRepo", "Database error: ${error.message}")
                    // Don't crash the app, just send empty list
                    trySend(emptyList())
                }
            })

        awaitClose { database.reference.child("products").removeEventListener(snapshotListener) }
    }



    override suspend fun getHotSaleProducts(): Flow<List<ProductModel>> = callbackFlow {
        val snapshotListener = productsRef
            .orderByChild("isOnSale")
            .equalTo(true)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val products = mutableListOf<ProductModel>()
                    for (childSnapshot in snapshot.children) {
                        val product = childSnapshot.getValue(ProductModel::class.java)
                        product?.let { products.add(it) }
                    }
                    trySend(products.sortedByDescending { it.timestamp })
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { productsRef.removeEventListener(snapshotListener) }
    }

    override suspend fun getProductById(productId: String): ProductModel? {
        return try {
            val snapshot = productsRef.child(productId).get().await()
            snapshot.getValue(ProductModel::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addProduct(product: ProductModel): Boolean {
        return try {
            val productId = if (product.id.isEmpty()) {
                productsRef.push().key ?: return false
            } else {
                product.id
            }

            val productWithId = product.copy(id = productId)
            productsRef.child(productId).setValue(productWithId).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateProduct(product: ProductModel): Boolean {
        return try {
            productsRef.child(product.id).setValue(product).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteProduct(productId: String): Boolean {
        return try {
            productsRef.child(productId).removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getProductsByCategory(category: String): Flow<List<ProductModel>> = callbackFlow {
        val snapshotListener = productsRef
            .orderByChild("category")
            .equalTo(category)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val products = mutableListOf<ProductModel>()
                    for (childSnapshot in snapshot.children) {
                        val product = childSnapshot.getValue(ProductModel::class.java)
                        product?.let { products.add(it) }
                    }
                    trySend(products.sortedByDescending { it.timestamp })
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { productsRef.removeEventListener(snapshotListener) }
    }

    override suspend fun getProductsBySeller(sellerId: String): Flow<List<ProductModel>> = callbackFlow {
        val snapshotListener = productsRef
            .orderByChild("sellerId")
            .equalTo(sellerId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val products = mutableListOf<ProductModel>()
                    for (childSnapshot in snapshot.children) {
                        val product = childSnapshot.getValue(ProductModel::class.java)
                        product?.let { products.add(it) }
                    }
                    trySend(products.sortedByDescending { it.timestamp })
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { productsRef.removeEventListener(snapshotListener) }
    }

    override suspend fun updateProduct(productId: String, updates: Map<String, Any?>) {
        try {
            productsRef.child(productId).updateChildren(updates).await()
        } catch (e: Exception) {
            throw e
        }
    }
}