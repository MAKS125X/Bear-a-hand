package com.example.simbirsoftmobile.di

import com.example.simbirsoftmobile.data.network.api.CategoryService
import com.example.simbirsoftmobile.data.network.api.EventService
import com.example.simbirsoftmobile.data.network.dtos.category.CategoryNetworkDeserializer
import com.example.simbirsoftmobile.data.network.dtos.event.EventNetworkDeserializer
import com.example.simbirsoftmobile.data.network.dtos.event.EventsNetworkDeserializer
import com.example.simbirsoftmobile.data.network.repositories.CategoryRepositoryNetwork
import com.example.simbirsoftmobile.data.network.repositories.EventRepositoryNetwork
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
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

    private fun provideEventRepository(): EventRepository {
        return EventRepositoryNetwork(eventService)
    }

    private fun provideCategoryRepository(): CategoryRepository {
        return CategoryRepositoryNetwork(categoryService)
    }

    private fun provideRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

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
            .client(client)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}
