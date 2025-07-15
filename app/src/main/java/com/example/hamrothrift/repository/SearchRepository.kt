package com.example.hamrothrift.repository

import com.example.hamrothrift.model.ProductModel
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchProducts(query: String): Flow<List<ProductModel>>
    suspend fun getSearchHistory(): Flow<List<String>>
    suspend fun saveSearchQuery(query: String)
    suspend fun clearSearchHistory()
}