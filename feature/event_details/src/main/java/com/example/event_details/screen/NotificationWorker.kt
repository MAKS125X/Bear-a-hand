package com.example.event_details.screen

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.ListenableWorker.Result
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    lateinit var notificationWork: (Context, String, String, Int) -> Result

    override fun doWork(): Result {
        val eventId = inputData.getString(MONEY_HELP_ID_KEY)
            ?: return Result.failure()
        val eventName = inputData.getString(MONEY_HELP_EVENT_NAME_KEY)
            ?: return Result.failure()
        val amount = inputData.getInt(MONEY_HELP_AMOUNT_KEY, 0)

        val result = notificationWork.invoke(applicationContext, eventId, eventName, amount)

        return result
    }

    companion object {
        const val MONEY_HELP_ID_KEY = "NotificationWorkerEventId"
        const val MONEY_HELP_EVENT_NAME_KEY = "NotificationWorkerEventName"
        const val MONEY_HELP_AMOUNT_KEY = "NotificationWorkerAmount"
    }
}

class HelpWorkerFactory(private val workerAction: (Context, String, String, Int) -> Result) :
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
                instance.notificationWork = workerAction
            }
        }

        return instance
    }
}

const val MoneyHelpNotificationIntentKey = "OpenEventDetailsFragment"
const val MoneyHelpNotificationIntentValue = "EventDetailsFragment"

const val MoneyHelpNotificationId = 123123
