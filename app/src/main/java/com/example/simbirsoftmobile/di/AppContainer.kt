package com.example.simbirsoftmobile.di

import android.content.Context
import androidx.room.Room
import com.example.simbirsoftmobile.data.local.AppDatabase
import com.example.simbirsoftmobile.data.local.TransactionProvider
import com.example.simbirsoftmobile.data.network.api.CategoryService
import com.example.simbirsoftmobile.data.network.api.EventService
import com.example.simbirsoftmobile.data.network.dtos.category.CategoryNetworkDeserializer
import com.example.simbirsoftmobile.data.network.dtos.event.EventNetworkDeserializer
import com.example.simbirsoftmobile.data.network.dtos.event.EventsNetworkDeserializer
import com.example.simbirsoftmobile.data.network.interceptors.networkMonitor.LiveNetworkMonitor
import com.example.simbirsoftmobile.data.network.interceptors.networkMonitor.NetworkMonitorInterceptor
import com.example.simbirsoftmobile.data.repositories.localWithFetch.CategoryRepositoryWithFetch
import com.example.simbirsoftmobile.data.repositories.localWithFetch.EventRepositoryWithFetch
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(private val context: Context) {
    val eventRepository by lazy {
        provideEventRepository()
    }

    val categoryRepository by lazy {
        provideCategoryRepository()
    }

    private val retrofit: Retrofit by lazy {
        provideRetrofit()
    }

    private val categoryService by lazy {
        retrofit.create(CategoryService::class.java)
    }

    private val eventService by lazy {
        retrofit.create(EventService::class.java)
    }

    private val db by lazy {
        provideDatabase()
    }

    private val transactionProvider by lazy {
        provideTransactionProvider()
    }

    private fun provideTransactionProvider(): TransactionProvider {
        return TransactionProvider(db)
    }

    private fun provideDatabase() =
        Room.databaseBuilder(
            context = context.applicationContext,
            AppDatabase::class.java,
            "events.db"
        ).build()

    private fun provideEventRepository(): EventRepository {
        return EventRepositoryWithFetch(eventService, db.events(), transactionProvider)
    }

    private fun provideCategoryRepository(): CategoryRepository {
        return CategoryRepositoryWithFetch(categoryService, db.categories(), transactionProvider)
    }

    private fun provideRetrofit(): Retrofit {
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
            .client(provideHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private fun provideHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(NetworkMonitorInterceptor(LiveNetworkMonitor(SimbirSoftApp.INSTANCE)))
            .build()
    }
}
