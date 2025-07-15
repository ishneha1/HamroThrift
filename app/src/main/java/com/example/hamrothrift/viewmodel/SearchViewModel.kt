package com.example.hamrothrift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hamrothrift.model.ProductModel
import com.example.hamrothrift.repository.SearchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: SearchRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<ProductModel>>(emptyList())
    val searchResults: StateFlow<List<ProductModel>> = _searchResults.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadSearchHistory()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query

        searchJob?.cancel()

        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // Debounce search
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                repository.searchProducts(query).collect { results ->
                    _searchResults.value = results
                    _isLoading.value = false

                    if (query.isNotBlank()) {
                        repository.saveSearchQuery(query)
                        loadSearchHistory()
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Search failed: ${e.message}"
                _isLoading.value = false
                _searchResults.value = emptyList()
            }
        }
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            repository.getSearchHistory().collect { history ->
                _searchHistory.value = history.reversed() // Show recent first
            }
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            repository.clearSearchHistory()
            _searchHistory.value = emptyList()
        }
    }

    fun selectFromHistory(query: String) {
        updateSearchQuery(query)
    }

    fun clearError() {
        _errorMessage.value = null
    }
}