package com.example.hamrothrift.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import com.example.hamrothrift.model.NavigationItem

interface NavigationRepo {
    fun getNavigationItems(): List<NavigationItem>
    fun getCurrentTabIndex(): Int
    fun updateCurrentTabIndex(index: Int)
}

class NavigationRepoImpl : NavigationRepo {
    private var currentTabIndex = 0

    override fun getNavigationItems(): List<NavigationItem> {
        return listOf(
            NavigationItem(label = "Home", icon = Icons.Default.Home, index = 0),
            NavigationItem(label = "Sale", icon = Icons.Default.Star, index = 1),
            NavigationItem(label = "Notification", icon = Icons.Default.Notifications, index = 2),
            NavigationItem(label = "Profile", icon = Icons.Default.Person, index = 3)
        )
    }

    override fun getCurrentTabIndex(): Int = currentTabIndex

    override fun updateCurrentTabIndex(index: Int) {
        currentTabIndex = index
    }
}