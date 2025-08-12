package com.example.hamrothrift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hamrothrift.model.CartItem
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CartRepository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    init {
        loadCartItems()
        loadCartItemCount()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getCartItems().collect { items ->
                    _cartItems.value = items
                    _totalPrice.value = items.sumOf { (it.product?.price ?: 0.0) * it.quantity }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load cart: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private fun loadCartItemCount() {
        viewModelScope.launch {
            try {
                repository.getCartItemCount().collect { count ->
                    _cartItemCount.value = count
                }
            } catch (e: Exception) {
                _cartItemCount.value = 0
            }
        }
    }

    fun addToCart(product: ProductModel, quantity: Int = 1) {
        viewModelScope.launch {
            try {
                repository.addToCart(product, quantity).collect { success ->
                    if (!success) {
                        _errorMessage.value = "Failed to add item to cart"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add item: ${e.message}"
            }
        }
    }

    fun updateQuantity(itemId: String, newQuantity: Int) {
        viewModelScope.launch {
            try {
                repository.updateQuantity(itemId, newQuantity).collect { success ->
                    if (!success) {
                        _errorMessage.value = "Failed to update quantity"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update quantity: ${e.message}"
            }
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            try {
                repository.removeItem(itemId).collect { success ->
                    if (!success) {
                        _errorMessage.value = "Failed to remove item"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to remove item: ${e.message}"
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                repository.clearCart().collect { success ->
                    if (!success) {
                        _errorMessage.value = "Failed to clear cart"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to clear cart: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

class CartViewModelFactory(
    private val repository: CartRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}