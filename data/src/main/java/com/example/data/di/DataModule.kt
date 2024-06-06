package com.example.data.di

import android.content.Context
import androidx.room.Room
import com.example.api.CategoryService
import com.example.api.EventService
import com.example.api.dtos.category.CategoryNetworkDeserializer
import com.example.api.dtos.event.EventNetworkDeserializer
import com.example.api.dtos.event.EventsNetworkDeserializer
import com.example.core.di.AppScope
import com.example.core.repositories.CategoryRepository
import com.example.core.repositories.EventRepository
import com.example.data.localWithFetch.CategoryRepositoryWithFetch
import com.example.data.localWithFetch.EventRepositoryWithFetch
import com.example.local.AppDatabase
import com.example.local.daos.CategoryDao
import com.example.local.daos.EventDao
import com.example.network.networkMonitor.LiveNetworkMonitor
import com.example.network.networkMonitor.NetworkMonitor
import com.example.network.networkMonitor.NetworkMonitorInterceptor
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module(includes = [DatabaseModule::class, NetworkModule::class, AppBindModule::class])
interface DataModule

@Module
class DatabaseModule {
    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao = appDatabase.categories()

    @Provides
    fun provideEventDao(appDatabase: AppDatabase): EventDao = appDatabase.events()

    @AppScope
    @Provides
    fun provideDatabase(
        context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context = context.applicationContext,
            AppDatabase::class.java,
            "events.db",
        )
            .build()
    }
}

@Module
class NetworkModule {
    @AppScope
    @Provides
    fun provideHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        liveNetworkMonitor: NetworkMonitorInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(liveNetworkMonitor)
            .build()
    }

    @Provides
    fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @AppScope
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit {
        val gson = GsonBuilder()
            .registerTypeAdapter(
                CategoryNetworkDeserializer.typeToken,
                CategoryNetworkDeserializer(),
            )
            .registerTypeAdapter(
                EventsNetworkDeserializer.typeToken,
                EventsNetworkDeserializer(),
            )
            .registerTypeAdapter(
                EventNetworkDeserializer.objectType,
                EventNetworkDeserializer(),
            )
            .create()

        return Retrofit.Builder()
            .baseUrl("https://mock.apidog.com/m1/509685-468980-default/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun provideCategoryService(retrofit: Retrofit): CategoryService =
        retrofit.create(CategoryService::class.java)

    @Provides
    fun provideEventService(retrofit: Retrofit): EventService =
        retrofit.create(EventService::class.java)
}

@Module
interface AppBindModule {
    @Suppress("FunctionName")
    @AppScope
    @Binds
    fun bindCategoryRepositoryWithFetch_to_CategoryRepository(
        categoryRepositoryWithFetch: CategoryRepositoryWithFetch,
    ): CategoryRepository


    @Suppress("FunctionName")
    @AppScope
    @Binds
    fun bindEventRepositoryWithFetch_to_EventRepository(
        eventRepositoryWithFetch: EventRepositoryWithFetch,
    ): EventRepository

    @Suppress("FunctionName")
    @Binds
    fun bindNetworkLiveMonitor_to_NetworkMonitor(
        networkLiveMonitor: LiveNetworkMonitor,
    ): NetworkMonitor
}
