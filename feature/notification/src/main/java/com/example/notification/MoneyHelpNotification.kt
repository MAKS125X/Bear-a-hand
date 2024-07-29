package com.example.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

data class MoneyHelpNotificationModel(
    val eventId: String,
    val eventName: String,
    val moneyAmount: Int,
    val intent: Intent,
)

fun getNotificationBuilder(
    context: Context,
    notificationModel: MoneyHelpNotificationModel
): NotificationCompat.Builder {
    notificationModel.intent
    val pendingIntent: PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            notificationModel.intent,
            PendingIntent.FLAG_IMMUTABLE
        )

    return NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(notificationModel.eventName)
        .setSmallIcon(com.example.common_view.R.mipmap.ic_launcher)
        .setContentText(context.getString(R.string.thanks_for_donate, notificationModel.moneyAmount))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
}