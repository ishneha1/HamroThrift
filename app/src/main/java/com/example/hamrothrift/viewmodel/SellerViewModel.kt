package com.example.hamrothrift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hamrothrift.model.SellerModel
import com.example.hamrothrift.repository.SellerRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SellerViewModel(private val repository: SellerRepo) : ViewModel() {
    private val _sellers = MutableStateFlow<List<SellerModel>>(emptyList())
    val sellers: StateFlow<List<SellerModel>> = _sellers

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadSellers()
    }

    private fun loadSellers() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getTopSellers().collect {
                _sellers.value = it
                _isLoading.value = false
            }
        }
    }
}

class SellerViewModelFactory(private val repository: SellerRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SellerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SellerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
