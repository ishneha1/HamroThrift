package com.example.hamrothrift.repository

import com.example.hamrothrift.model.ProductModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductRepoImpl : ProductRepo {
    private val firestore = FirebaseFirestore.getInstance()
    private val productsCollection = firestore.collection("products")

    override suspend fun getAllProducts(): Flow<List<ProductModel>> = callbackFlow {
        val snapshotListener = productsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val products = snapshot.documents.mapNotNull { document ->
                        document.toObject(ProductModel::class.java)
                    }
                    trySend(products)
                }
            }
        awaitClose { snapshotListener.remove() }
    }

    override suspend fun getHotSaleProducts(): Flow<List<ProductModel>> = callbackFlow {
        val snapshotListener = productsCollection
            .whereEqualTo("isHotSale", true)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val products = snapshot.documents.mapNotNull { document ->
                        document.toObject(ProductModel::class.java)
                    }
                    trySend(products)
                }
            }
        awaitClose { snapshotListener.remove() }
    }

    override suspend fun getProductById(productId: String): ProductModel? {
        return try {
            val document = productsCollection.document(productId).get().await()
            document.toObject(ProductModel::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addProduct(product: ProductModel): Boolean {
        return try {
            productsCollection.add(product).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateProduct(product: ProductModel): Boolean {
        return try {
            productsCollection.document(product.id).set(product).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteProduct(productId: String): Boolean {
        return try {
            productsCollection.document(productId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getProductsByCategory(category: String): Flow<List<ProductModel>> = callbackFlow {
        val snapshotListener = productsCollection
            .whereEqualTo("category", category)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val products = snapshot.documents.mapNotNull { document ->
                        document.toObject(ProductModel::class.java)
                    }
                    trySend(products)
                }
            }
        awaitClose { snapshotListener.remove() }
    }

    override suspend fun getProductsBySeller(sellerId: String): Flow<List<ProductModel>> = callbackFlow {
        val snapshotListener = productsCollection
            .whereEqualTo("sellerId", sellerId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val products = snapshot.documents.mapNotNull { document ->
                        document.toObject(ProductModel::class.java)
                    }
                    trySend(products)
                }
            }
        awaitClose { snapshotListener.remove() }
    }
}