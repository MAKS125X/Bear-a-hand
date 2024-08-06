package com.example.simbirsoftmobile.di

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import com.example.auth.di.AuthDepsStore
import com.example.category_settings.di.FilterDepsStore
import com.example.content_holder.di.ContentDepsStore
import com.example.event_details.di.EventDepsStore
import com.example.help.di.HelpDepsStore
import com.example.news.di.NewsDepsStore
import com.example.notification.createNotificationChannel
import com.example.search.di.SearchCDepsStore
import javax.inject.Inject

class SimbirSoftApp() : Application(), ImageLoaderFactory {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .context(this)
            .build()
    }

    @Inject
    lateinit var workerFactory: WorkerFactory

    override fun onCreate() {
        super.onCreate()

        appComponent.inject(this)

        HelpDepsStore.deps = appComponent
        NewsDepsStore.deps = appComponent
        EventDepsStore.deps = appComponent
        AuthDepsStore.deps = appComponent
        ContentDepsStore.deps = appComponent
        FilterDepsStore.deps = appComponent
        SearchCDepsStore.deps = appComponent

        createNotificationChannel()

        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        )
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader
            .Builder(this)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()
    }
}
