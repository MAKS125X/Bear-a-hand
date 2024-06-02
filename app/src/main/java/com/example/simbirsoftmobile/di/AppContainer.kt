package com.example.simbirsoftmobile.di

import android.content.Context
import androidx.room.Room
import com.example.simbirsoftmobile.data.local.AppDatabase
import com.example.simbirsoftmobile.data.local.daos.CategoryDao
import com.example.simbirsoftmobile.data.local.daos.EventDao
import com.example.simbirsoftmobile.data.network.api.CategoryService
import com.example.simbirsoftmobile.data.network.api.EventService
import com.example.simbirsoftmobile.data.network.dtos.category.CategoryNetworkDeserializer
import com.example.simbirsoftmobile.data.network.dtos.event.EventNetworkDeserializer
import com.example.simbirsoftmobile.data.network.dtos.event.EventsNetworkDeserializer
import com.example.simbirsoftmobile.data.network.interceptors.networkMonitor.LiveNetworkMonitor
import com.example.simbirsoftmobile.data.network.interceptors.networkMonitor.NetworkMonitor
import com.example.simbirsoftmobile.data.network.interceptors.networkMonitor.NetworkMonitorInterceptor
import com.example.simbirsoftmobile.data.repositories.localWithFetch.CategoryRepositoryWithFetch
import com.example.simbirsoftmobile.data.repositories.localWithFetch.EventRepositoryWithFetch
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.example.simbirsoftmobile.presentation.screens.content.ContentFragment
import com.example.simbirsoftmobile.presentation.screens.eventDetails.EventDetailsFragment
import com.example.simbirsoftmobile.presentation.screens.filter.FilterFragment
import com.example.simbirsoftmobile.presentation.screens.help.HelpFragment
import com.example.simbirsoftmobile.presentation.screens.news.NewsFragment
import com.example.simbirsoftmobile.presentation.screens.search.SearchFragment
import com.example.simbirsoftmobile.presentation.screens.search.events.EventsFragment
import com.example.simbirsoftmobile.presentation.screens.search.organizations.OrganizationsFragment
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(fragment: HelpFragment)
    fun inject(fragment: FilterFragment)
    fun inject(fragment: NewsFragment)
    fun inject(fragment: EventDetailsFragment)
    fun inject(fragment: OrganizationsFragment)
    fun inject(fragment: EventsFragment)
    fun inject(fragment: SearchFragment)
    fun inject(contentFragment: ContentFragment)
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is SimbirSoftApp -> appComponent
        else -> this.applicationContext.appComponent
    }

@Module(includes = [NetworkModule::class, DatabaseModule::class, AppBindModule::class])
interface AppModule

@Module
class DatabaseModule {
    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao = appDatabase.categories()

    @Provides
    fun provideEventDao(appDatabase: AppDatabase): EventDao = appDatabase.events()

    @Singleton
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
    @Singleton
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

    @Singleton
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
    @Singleton
    @Binds
    fun bindCategoryRepositoryWithFetch_to_CategoryRepository(
        categoryRepositoryWithFetch: CategoryRepositoryWithFetch,
    ): CategoryRepository


    @Suppress("FunctionName")
    @Singleton
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
