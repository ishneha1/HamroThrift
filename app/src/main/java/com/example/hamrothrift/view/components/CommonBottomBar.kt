package com.example.hamrothrift.view.components

import android.app.Activity
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.hamrothrift.navigation.NavigationHandler
import com.example.hamrothrift.view.theme.ui.theme.appBar
import com.example.hamrothrift.viewmodel.NavigationViewModel

@Composable
fun CommonBottomBar(
    navigationViewModel: NavigationViewModel,
    selectedTab: Int
) {
    val context = LocalContext.current
    val activity = context as? Activity

    NavigationBar(containerColor = appBar) {
        navigationViewModel.navigationItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selectedTab == item.index,
                onClick = {
                    navigationViewModel.updateTab(item.index)
                    NavigationHandler.navigateToScreen(context, activity, item.index)
                }
            )
        }
    }
}