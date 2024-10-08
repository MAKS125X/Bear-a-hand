package com.example.event_details.screen.notification

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.core.repositories.SettingsRepository
import com.example.event_details.usecase.GetNotificationStatusUseCase

class NotificationWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
) :
    Worker(appContext, workerParams) {

    lateinit var activityIntent: (String) -> Intent
    lateinit var getNotificationStatusUseCase: GetNotificationStatusUseCase

    override fun doWork(): Result {
        val eventId = inputData.getString(MONEY_HELP_ID_KEY)
            ?: return Result.failure()
        val eventName = inputData.getString(MONEY_HELP_EVENT_NAME_KEY)
            ?: return Result.failure()
        val amount = inputData.getInt(MONEY_HELP_AMOUNT_KEY, 0)
        val isRepeated = inputData.getBoolean(IS_REPEATED_NOTIFICATION_KEY, true)

        return if (ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED && getNotificationStatusUseCase.invoke()
        ) {
            val builder = getNotificationBuilder(
                appContext,
                MoneyHelpNotificationModel(
                    eventId,
                    eventName,
                    amount,
                    activityIntent.invoke(eventId)
                ),
                isRepeated,
            )

            with(NotificationManagerCompat.from(appContext)) {
                notify(eventId.hashCode(), builder.build())
            }

            Result.success()
        } else {
            Result.failure()
        }
    }

    companion object {
        const val MONEY_HELP_ID_KEY = "NotificationWorkerEventId"
        const val MONEY_HELP_EVENT_NAME_KEY = "NotificationWorkerEventName"
        const val MONEY_HELP_AMOUNT_KEY = "NotificationWorkerAmount"
        const val IS_REPEATED_NOTIFICATION_KEY = "NotificationWorkerIsRepeated"
    }
}

class HelpWorkerFactory(
    private val settingsRepository: SettingsRepository,
    private val intentById: (String) -> Intent,
) :
    WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val workerKlass = Class.forName(workerClassName).asSubclass(Worker::class.java)
        val constructor =
            workerKlass.getDeclaredConstructor(Context::class.java, WorkerParameters::class.java)
        val instance = constructor.newInstance(appContext, workerParameters)

        when (instance) {
            is NotificationWorker -> {
                instance.activityIntent = intentById
                instance.getNotificationStatusUseCase =
                    GetNotificationStatusUseCase(settingsRepository)
            }
        }

        return instance
    }
}

const val MoneyHelpNotificationIntentKey = "OpenEventDetailsFragment"
const val MoneyHelpNotificationIntentValue = "EventDetailsFragment"
