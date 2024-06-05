package com.example.simbirsoftmobile.di

import android.content.Context
import com.example.auth.di.AuthDeps
import com.example.auth.di.AuthNavigation
import com.example.category_settings.di.FilterDeps
import com.example.content_holder.di.ContentDeps
import com.example.content_holder.di.ContentHolderNavigation
import com.example.core.di.AppScope
import com.example.core.repositories.CategoryRepository
import com.example.core.repositories.EventRepository
import com.example.data.di.DataModule
import com.example.event_details.di.EventDetailsDeps
import com.example.help.di.HelpDeps
import com.example.news.di.NewsComponentNavigation
import com.example.news.di.NewsDeps
import com.example.search.di.SearchDeps
import com.example.simbirsoftmobile.di.modules.AuthDepsModule
import com.example.simbirsoftmobile.di.modules.ContentHolderDepsModule
import com.example.simbirsoftmobile.di.modules.NewsDepsModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module

@Component(modules = [AppModule::class, NewsDepsModule::class, AuthDepsModule::class, ContentHolderDepsModule::class, AuthDepsModule::class])
@AppScope
interface AppComponent : HelpDeps, NewsDeps, EventDetailsDeps, ContentDeps, AuthDeps, SearchDeps, FilterDeps {
    override val repository: CategoryRepository
    override val categoryRepository: CategoryRepository
    override val eventRepository: EventRepository
    override val contentHolderNavigation: ContentHolderNavigation
    override val newsComponentNavigation: NewsComponentNavigation
    override val authNavigation: AuthNavigation

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is SimbirSoftApp -> appComponent
        else -> this.applicationContext.appComponent
    }

@Module(includes = [DataModule::class])
interface AppModule



