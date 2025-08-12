package com.example.hamrothrift.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.hamrothrift.model.ProductModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SearchRepositoryImpl(private val context: Context) : SearchRepository {
    private val database = FirebaseDatabase.getInstance()
    private val productsRef = database.reference.child("products")
    private val prefs: SharedPreferences = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)

    override suspend fun searchProducts(query: String): Flow<List<ProductModel>> = callbackFlow {
        try {
            val results = mutableListOf<ProductModel>()
            val searchQuery = query.lowercase()

            // Search by name (contains search)
            val nameSnapshot = productsRef.get().await()
            for (childSnapshot in nameSnapshot.children) {
                val product = childSnapshot.getValue(ProductModel::class.java)
                product?.let {
                    if (it.name.lowercase().contains(searchQuery) ||
                        it.category.lowercase().contains(searchQuery) ||
                        it.description.lowercase().contains(searchQuery)) {
                        results.add(it.copy(id = childSnapshot.key ?: ""))
                    }
                }
            }

            trySend(results.distinctBy { it.id }.take(20))
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

        historyList.remove(query)
        historyList.add(query)

        val updatedHistory = historyList.takeLast(10).toSet()
        prefs.edit().putStringSet("search_history", updatedHistory).apply()
    }

    override suspend fun clearSearchHistory() {
        prefs.edit().remove("search_history").apply()
    }
}