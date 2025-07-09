package com.example.hamrothrift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.repository.ProductRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepo) : ViewModel() {
    private val _products = MutableStateFlow<List<ProductModel>>(emptyList())
    val products: StateFlow<List<ProductModel>> = _products

    private val _hotSaleProducts = MutableStateFlow<List<ProductModel>>(emptyList())
    val hotSaleProducts: StateFlow<List<ProductModel>> = _hotSaleProducts

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadProducts()
        loadHotSaleProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllProducts()
                    .catch { e ->
                        _error.value = e.message
                    }
                    .collect { productsList ->
                        _products.value = productsList
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    private fun loadHotSaleProducts() {
        viewModelScope.launch {
            try {
                repository.getHotSaleProducts()
                    .catch { e ->
                        _error.value = e.message
                    }
                    .collect { hotSales ->
                        _hotSaleProducts.value = hotSales
                    }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun getProductsByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getProductsByCategory(category)
                    .catch { e ->
                        _error.value = e.message
                    }
                    .collect { productsList ->
                        _products.value = productsList
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun addProduct(product: ProductModel) {
        viewModelScope.launch {
            try {
                repository.addProduct(product)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateProduct(product: ProductModel) {
        viewModelScope.launch {
            try {
                repository.updateProduct(product)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(productId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}

class ProductViewModelFactory(private val repository: ProductRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}