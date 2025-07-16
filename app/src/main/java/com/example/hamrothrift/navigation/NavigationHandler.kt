package com.example.hamrothrift.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.hamrothrift.view.ProfileActivity
import com.example.hamrothrift.view.buy.DashboardActivityBuy
import com.example.hamrothrift.view.NotificationActivity
import com.example.hamrothrift.view.buy.SaleActivity

object NavigationHandler {
    fun navigateToScreen(context: Context, currentActivity: Activity?, index: Int) {
        val intent = when (index) {
            0 -> Intent(context, DashboardActivityBuy::class.java)
            1 -> Intent(context, SaleActivity::class.java)
            2 -> Intent(context, NotificationActivity::class.java)
            3 -> Intent(context, ProfileActivity::class.java)
            else -> return
        }
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        context.startActivity(intent)
        currentActivity?.finish()
    }
}