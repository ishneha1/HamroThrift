package com.example.hamrothrift.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hamrothrift.repository.NavigationRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationViewModel(
    private val navigationRepo: NavigationRepo
) : ViewModel() {
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab

    val navigationItems = navigationRepo.getNavigationItems()

    fun updateTab(index: Int) {
        _currentTab.value = index
    }
}

class NavigationViewModelFactory(
    private val navigationRepo: NavigationRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NavigationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NavigationViewModel(navigationRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}