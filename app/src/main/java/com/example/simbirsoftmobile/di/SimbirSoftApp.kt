package com.example.simbirsoftmobile.di

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import com.example.event_details.di.EventDepsStore
import com.example.help.di.HelpDepsStore
import com.example.news.di.NewsDepsStore

class SimbirSoftApp : Application(), ImageLoaderFactory {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .context(this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        HelpDepsStore.deps = appComponent
        NewsDepsStore.deps = appComponent
        EventDepsStore.deps = appComponent
    }

    override fun newImageLoader(): ImageLoader {
        //TODO: Add DI
        return ImageLoader
            .Builder(this)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()
    }
}
