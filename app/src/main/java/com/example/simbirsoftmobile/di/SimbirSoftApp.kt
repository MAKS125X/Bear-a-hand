package com.example.simbirsoftmobile.di

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import com.example.auth.di.AuthDepsStore
import com.example.category_settings.di.FilterDepsStore
import com.example.content_holder.di.ContentDepsStore
import com.example.event_details.di.EventDepsStore
import com.example.help.di.HelpDepsStore
import com.example.news.di.NewsDepsStore
import com.example.search.di.SearchCDepsStore
import com.example.search.di.SearchDeps
import com.example.simbirsoftmobile.di.modules.AuthDepsModule

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
        AuthDepsStore.deps = appComponent
        ContentDepsStore.deps = appComponent
        FilterDepsStore.deps = appComponent
        SearchCDepsStore.deps = appComponent
    }

    override fun newImageLoader(): ImageLoader {
        //TODO: Add DI
        return ImageLoader
            .Builder(this)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()
    }
}
