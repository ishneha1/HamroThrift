package com.example.hamrothrift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hamrothrift.model.CartItem
import com.example.hamrothrift.model.PaymentResult
import com.example.hamrothrift.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val repository: PaymentRepository
) : ViewModel() {

    private val _paymentState = MutableStateFlow<PaymentResult?>(null)
    val paymentState: StateFlow<PaymentResult?> = _paymentState.asStateFlow()

    fun processPayment(items: List<CartItem>, totalAmount: Double, paymentMethod: String) {
        viewModelScope.launch {
            _paymentState.value = PaymentResult.Loading
            try {
                repository.processPayment(items, totalAmount, paymentMethod).collect { result ->
                    _paymentState.value = result
                }
            } catch (e: Exception) {
                _paymentState.value = PaymentResult.Error("Payment failed: ${e.message}")
            }
        }
    }

    fun clearPaymentState() {
        _paymentState.value = null
    }
}

class PaymentViewModelFactory(
    private val repository: PaymentRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}