package com.example.simbirsoftmobile.di

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy

class SimbirSoftApp : Application(), ImageLoaderFactory {
    val appComponent: AppComponent by lazy(LazyThreadSafetyMode.NONE) {
        DaggerAppComponent.factory().create(this)
    }

    override fun newImageLoader(): ImageLoader {
        //TODO: Add DI
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
