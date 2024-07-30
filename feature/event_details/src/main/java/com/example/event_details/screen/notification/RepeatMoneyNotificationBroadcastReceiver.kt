package com.example.event_details.screen.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.event_details.screen.NotificationWorker
import java.util.concurrent.TimeUnit

class RepeatMoneyNotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == REPEAT_NOTIFICATION) {
            val eventId = intent.getStringExtra(EVENT_ID_KEY)
            val eventName = intent.getStringExtra(EVENT_NAME_KEY)
            val moneyAmount = intent.getIntExtra(MONEY_AMOUNT_KEY, 0)

            val data = Data.Builder().apply {
                putString(NotificationWorker.MONEY_HELP_ID_KEY, eventId)
                putString(NotificationWorker.MONEY_HELP_EVENT_NAME_KEY, eventName)
                putInt(NotificationWorker.MONEY_HELP_AMOUNT_KEY, moneyAmount)
                putBoolean(NotificationWorker.IS_REPEATED_NOTIFICATION_KEY, true)
            }

            context?.let {
                NotificationManagerCompat.from(it).cancel(eventId.hashCode())

                val request = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(30, TimeUnit.MINUTES)
                    .setInputData(data.build())
                    .build()

                WorkManager.getInstance(it).enqueue(request)
            }
        }
    }

    companion object {
        const val EVENT_ID_KEY = "EventId"
        const val EVENT_NAME_KEY = "EventName"
        const val MONEY_AMOUNT_KEY = "MoneyAmount"
    }
}
