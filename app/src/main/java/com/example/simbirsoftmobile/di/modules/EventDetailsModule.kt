package com.example.simbirsoftmobile.di.modules

import android.content.Context
import android.content.Intent
import androidx.work.WorkerFactory
import com.example.event_details.di.WorkerDeps
import com.example.event_details.screen.notification.HelpWorkerFactory
import com.example.event_details.screen.notification.MoneyHelpNotificationIntentKey
import com.example.event_details.screen.notification.MoneyHelpNotificationIntentValue
import com.example.event_details.screen.notification.NotificationWorker
import com.example.simbirsoftmobile.MainActivity
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module(includes = [WorkerModule::class])
interface EventDetailsModule {
    @Binds
    fun bindNotificationNavigation(notificationNavigation: WorkerDepsImpl): WorkerDeps
}

@Module
class WorkerModule {
    @Provides
    fun workerFactory(notificationNavigation: WorkerDeps): WorkerFactory {
        return HelpWorkerFactory { eventId: String ->
            notificationNavigation.openEventDetailsEventIntent(
                eventId
            )
        }
    }
}

class WorkerDepsImpl @Inject constructor(
    private val context: Context,
) : WorkerDeps {
    override fun openEventDetailsEventIntent(eventId: String): Intent =
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(MoneyHelpNotificationIntentKey, MoneyHelpNotificationIntentValue)
            putExtra(NotificationWorker.MONEY_HELP_ID_KEY, eventId)
        }
}
