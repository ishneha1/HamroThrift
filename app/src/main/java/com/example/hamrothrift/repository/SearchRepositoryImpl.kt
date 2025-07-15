package com.example.hamrothrift.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.hamrothrift.model.ProductModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SearchRepositoryImpl(private val context: Context) : SearchRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val prefs: SharedPreferences = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)

    override suspend fun searchProducts(query: String): Flow<List<ProductModel>> = callbackFlow {
        try {
            val results = mutableListOf<ProductModel>()
            val searchQuery = query.lowercase()

            // Search by name
            val nameQuery = firestore.collection("products")
                .orderBy("name")
                .startAt(searchQuery)
                .endAt(searchQuery + "\uf8ff")
                .limit(20)

            val nameSnapshot = nameQuery.get().await()
            nameSnapshot.documents.forEach { document ->
                document.toObject(ProductModel::class.java)?.let { product ->
                    results.add(product.copy(id = document.id))
                }
            }

            // Search by category
            val categoryQuery = firestore.collection("products")
                .whereEqualTo("category", searchQuery)
                .limit(10)

            val categorySnapshot = categoryQuery.get().await()
            categorySnapshot.documents.forEach { document ->
                document.toObject(ProductModel::class.java)?.let { product ->
                    if (results.none { it.id == document.id }) {
                        results.add(product.copy(id = document.id))
                    }
                }
            }

            trySend(results.distinctBy { it.id })
        } catch (e: Exception) {
            trySend(emptyList())
        }

        awaitClose()
    }

    override suspend fun getSearchHistory(): Flow<List<String>> = callbackFlow {
        val history = prefs.getStringSet("search_history", emptySet())?.toList() ?: emptyList()
        trySend(history)
        awaitClose()
    }

    override suspend fun saveSearchQuery(query: String) {
        val currentHistory = prefs.getStringSet("search_history", mutableSetOf()) ?: mutableSetOf()
        val historyList = currentHistory.toMutableList()

        // Remove if already exists to avoid duplicates
        historyList.remove(query)
        // Add to the end
        historyList.add(query)

        // Keep only last 10 searches
        val updatedHistory = historyList.takeLast(10).toSet()
        prefs.edit().putStringSet("search_history", updatedHistory).apply()
    }

    override suspend fun clearSearchHistory() {
        prefs.edit().remove("search_history").apply()
    }
}