package com.example.hamrothrift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hamrothrift.model.Order
import com.example.hamrothrift.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllOrders()
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { ordersList ->
                    _orders.value = ordersList
                    _isLoading.value = false
                }
        }
    }

    fun addOrder(order: Order) {
        viewModelScope.launch {
            repository.addOrder(order)
        }
    }

    fun updateOrder(order: Order) {
        viewModelScope.launch {
            repository.updateOrder(order)
        }
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            repository.deleteOrder(orderId)
        }
    }
}

class OrderViewModelFactory(private val repository: OrderRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}