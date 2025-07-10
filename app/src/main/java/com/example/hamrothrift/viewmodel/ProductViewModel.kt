package com.example.hamrothrift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hamrothrift.model.ChatMessage
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.repository.ChatRepo
import com.example.hamrothrift.repository.ChatRepoImpl
import com.example.hamrothrift.repository.ProductRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepo,
                       private val chatRepo: ChatRepo = ChatRepoImpl()) : ViewModel()
{
    private val _products = MutableStateFlow<List<ProductModel>>(emptyList())
    val products: StateFlow<List<ProductModel>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var isLoadingMore = false
    private var lastLoadedTimestamp: Long? = null

    fun loadInitialProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllProducts().collect { productsList ->
                    _products.value = productsList
                    lastLoadedTimestamp = productsList.lastOrNull()?.timestamp
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMoreProducts() {
        if (isLoadingMore) return

        viewModelScope.launch {
            isLoadingMore = true
            _isLoading.value = true
            try {
                lastLoadedTimestamp?.let { timestamp ->
                    repository.getAllProducts().collect { newProducts ->
                        val filteredProducts = newProducts.filter { it.timestamp < timestamp }
                        _products.value = _products.value + filteredProducts
                        lastLoadedTimestamp = filteredProducts.lastOrNull()?.timestamp
                    }
                }
            } finally {
                _isLoading.value = false
                isLoadingMore = false
            }
        }
    }
    fun sendMessageToSeller(message: ChatMessage) {
        viewModelScope.launch {
            try {
                chatRepo.sendMessage(message)
            } catch (e: Exception) {
                // Handle error
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