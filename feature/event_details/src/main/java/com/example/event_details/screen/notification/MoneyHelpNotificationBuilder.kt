package com.example.event_details.screen.notification

import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.notification.CHANNEL_ID
import com.example.notification.R

const val REPEAT_NOTIFICATION = "RepeatNotification"

fun getNotificationBuilder(
    context: Context,
    notificationModel: MoneyHelpNotificationModel,
    isRepeated: Boolean,
): NotificationCompat.Builder {
    val openEventDetailsPendingIntent: PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            notificationModel.intent,
            PendingIntent.FLAG_IMMUTABLE
        )

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(notificationModel.eventName)
        .setSmallIcon(com.example.common_view.R.mipmap.ic_launcher)
        .setContentText(
            if (isRepeated) {
                context.getString(
                    R.string.repeat_thanks_for_donate
                )
            } else {
                context.getString(
                    R.string.thanks_for_donate,
                    notificationModel.moneyAmount
                )
            }
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(openEventDetailsPendingIntent)
        .setAutoCancel(true)

    if (!isRepeated) {
        val repeatBroadcastIntent =
            Intent(context, RepeatMoneyNotificationBroadcastReceiver::class.java).apply {
                action = REPEAT_NOTIFICATION
                putExtra(EXTRA_NOTIFICATION_ID, 0)
                putExtra(
                    RepeatMoneyNotificationBroadcastReceiver.EVENT_ID_KEY,
                    notificationModel.eventId,
                )
                putExtra(
                    RepeatMoneyNotificationBroadcastReceiver.EVENT_NAME_KEY,
                    notificationModel.eventName,
                )
                putExtra(
                    RepeatMoneyNotificationBroadcastReceiver.MONEY_AMOUNT_KEY,
                    notificationModel.moneyAmount,
                )
            }

        val repeatPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                context,
                0,
                repeatBroadcastIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

        builder.addAction(
            com.example.common_view.R.mipmap.ic_launcher,
            "Напомнить позже",
            repeatPendingIntent
        )
    }

    return builder
}
