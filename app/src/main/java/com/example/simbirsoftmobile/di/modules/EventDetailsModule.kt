package com.example.simbirsoftmobile.di.modules

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ListenableWorker.Result
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.example.event_details.di.WorkerDeps
import com.example.event_details.screen.HelpWorkerFactory
import com.example.event_details.screen.MoneyHelpNotificationId
import com.example.event_details.screen.MoneyHelpNotificationIntentKey
import com.example.event_details.screen.MoneyHelpNotificationIntentValue
import com.example.event_details.screen.NotificationWorker
import com.example.notification.MoneyHelpNotificationModel
import com.example.notification.getNotificationBuilder
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
    fun workerFactory(notificationNavigation: WorkerDeps):
            WorkerFactory {
        return HelpWorkerFactory(notificationNavigation.workerAction)
    }
}

class WorkerDepsImpl @Inject constructor(
    private val context: Context,
) : WorkerDeps {
    override val workerAction: (Context, String, String, Int) -> Result
        get() = { context: Context, id: String, name: String, amount: Int ->
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra(MoneyHelpNotificationIntentKey, MoneyHelpNotificationIntentValue)
                    putExtra(NotificationWorker.MONEY_HELP_ID_KEY, id)
                    putExtra(NotificationWorker.MONEY_HELP_EVENT_NAME_KEY, name)
                    putExtra(NotificationWorker.MONEY_HELP_AMOUNT_KEY, amount)
                }

                val builder = getNotificationBuilder(
                    context,
                    MoneyHelpNotificationModel(id, name, amount, intent),
                )

                with(NotificationManagerCompat.from(context)) {
                    notify(MoneyHelpNotificationId, builder.build())
                }

                Result.success()
            } else {
                Result.failure()
            }

        }

    override fun launchWorker(eventId: String, eventName: String, eventAmount: Int) {
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        val data = Data.Builder().apply {
            putString(NotificationWorker.MONEY_HELP_ID_KEY, eventId)
            putString(NotificationWorker.MONEY_HELP_EVENT_NAME_KEY, eventName)
            putInt(NotificationWorker.MONEY_HELP_AMOUNT_KEY, eventAmount)
        }

        val request = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(data.build())
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }
}