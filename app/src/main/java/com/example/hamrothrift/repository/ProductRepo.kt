package com.example.hamrothrift.repository

import com.example.hamrothrift.model.ProductModel
import kotlinx.coroutines.flow.Flow

interface ProductRepo {
    suspend fun getAllProducts(): Flow<List<ProductModel>>
    suspend fun getHotSaleProducts(): Flow<List<ProductModel>>
    suspend fun getProductById(productId: String): ProductModel?
    suspend fun addProduct(product: ProductModel): Boolean
    suspend fun updateProduct(product: ProductModel): Boolean
    suspend fun deleteProduct(productId: String): Boolean
    suspend fun getProductsByCategory(category: String): Flow<List<ProductModel>>
    suspend fun getProductsBySeller(sellerId: String): Flow<List<ProductModel>>
}