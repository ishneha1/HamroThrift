package com.example.hamrothrift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hamrothrift.model.SalesOverview
import com.example.hamrothrift.repository.SalesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SalesOverviewViewModel(private val repository: SalesRepository) : ViewModel() {

    private val _salesOverview = MutableStateFlow<SalesOverview?>(null)
    val salesOverview: StateFlow<SalesOverview?> = _salesOverview

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _selectedTimeRange = MutableStateFlow("7 Days")
    val selectedTimeRange: StateFlow<String> = _selectedTimeRange

    private val _selectedMode = MutableStateFlow("Sell")
    val selectedMode: StateFlow<String> = _selectedMode

    fun loadSalesData(timeRange: String) {
        _selectedTimeRange.value = timeRange
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            repository.getSalesOverview(timeRange)
                .onSuccess { overview ->
                    _salesOverview.value = overview
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Failed to load sales data"
                    _isLoading.value = false
                }
        }
    }

    fun setSelectedMode(mode: String) {
        _selectedMode.value = mode
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

class SalesOverviewViewModelFactory(private val repository: SalesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SalesOverviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SalesOverviewViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}