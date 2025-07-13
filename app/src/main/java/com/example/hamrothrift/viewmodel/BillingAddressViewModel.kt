package com.example.hamrothrift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hamrothrift.model.BillingAddress
import com.example.hamrothrift.repository.BillingAddressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BillingAddressViewModel(private val repository: BillingAddressRepository) : ViewModel() {

    private val _billingAddress = MutableStateFlow<BillingAddress?>(null)
    val billingAddress: StateFlow<BillingAddress?> = _billingAddress

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    fun loadBillingAddress(userId: String) {
        _isLoading.value = true
        _message.value = null

        viewModelScope.launch {
            repository.getBillingAddress(userId)
                .onSuccess { billingAddress ->
                    _billingAddress.value = billingAddress
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _message.value = exception.message ?: "Failed to load billing address"
                    _isLoading.value = false
                }
        }
    }

    fun saveBillingAddress(billingAddress: BillingAddress) {
        _isLoading.value = true
        _message.value = null

        viewModelScope.launch {
            repository.saveBillingAddress(billingAddress)
                .onSuccess {
                    _billingAddress.value = billingAddress
                    _message.value = "Billing address saved successfully!"
                    _isSuccess.value = true
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _message.value = exception.message ?: "Failed to save billing address"
                    _isLoading.value = false
                }
        }
    }

    fun deleteBillingAddress(userId: String) {
        _isLoading.value = true
        _message.value = null

        viewModelScope.launch {
            repository.deleteBillingAddress(userId)
                .onSuccess {
                    _billingAddress.value = null
                    _message.value = "Billing address deleted successfully!"
                    _isSuccess.value = true
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _message.value = exception.message ?: "Failed to delete billing address"
                    _isLoading.value = false
                }
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    fun resetSuccess() {
        _isSuccess.value = false
    }
}

class BillingAddressViewModelFactory(private val repository: BillingAddressRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BillingAddressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BillingAddressViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}