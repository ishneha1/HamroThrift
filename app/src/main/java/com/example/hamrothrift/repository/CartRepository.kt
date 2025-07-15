package com.example.hamrothrift.repository

import com.example.hamrothrift.model.CartItem
import com.example.hamrothrift.model.ProductModel
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun addToCart(product: ProductModel, quantity: Int): Flow<Boolean>
    suspend fun getCartItems(): Flow<List<CartItem>>
    suspend fun updateQuantity(itemId: String, newQuantity: Int): Flow<Boolean>
    suspend fun removeItem(itemId: String): Flow<Boolean>
    suspend fun clearCart(): Flow<Boolean>
    suspend fun getCartItemCount(): Flow<Int>
}