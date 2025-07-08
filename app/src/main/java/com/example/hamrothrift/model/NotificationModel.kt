package com.example.hamrothrift.model

import androidx.compose.ui.graphics.vector.ImageVector

data class NotificationModel(
    var notificationId: String = "",
    var title: String = "",
    var message: String = "",
    var time: String = "",
    var userId: String = "",
    var type: String = ""  // Can be "ORDER", "MESSAGE", "OFFER" etc.
)