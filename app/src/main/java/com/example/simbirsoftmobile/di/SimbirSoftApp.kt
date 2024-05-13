package com.example.simbirsoftmobile.di

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy

class SimbirSoftApp : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    val appContainer = AppContainer(this)

    override fun newImageLoader(): ImageLoader {
        return ImageLoader
            .Builder(this)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()
    }

    companion object {
        internal lateinit var INSTANCE: SimbirSoftApp
            private set
    }
}
