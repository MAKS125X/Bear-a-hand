package com.example.android_settings.di

import android.content.Context
import com.example.android_settings.AndroidSettingsRepository
import com.example.core.repositories.SettingsRepository
import dagger.Module
import dagger.Provides

@Module
class SettingsModule {
    @Provides
    fun provideSettingsRepository(context: Context): SettingsRepository =
        AndroidSettingsRepository(context)
}