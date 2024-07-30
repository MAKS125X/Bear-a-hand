package com.example.event_details.screen.notification

import android.content.Intent

data class MoneyHelpNotificationModel(
    val eventId: String,
    val eventName: String,
    val moneyAmount: Int,
    val intent: Intent,
)
