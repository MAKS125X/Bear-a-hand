package com.example.simbirsoftmobile.di.modules

import androidx.fragment.app.FragmentManager
import com.example.category_settings.screen.FilterFragment
import com.example.event_details.screen.EventDetailsFragment
import com.example.news.di.NewsComponentNavigation
import com.example.simbirsoftmobile.R
import dagger.Binds
import dagger.Module
import javax.inject.Inject

@Module
interface NewsDepsModule {
    @Binds
    fun bindNavigation(moveToEventDetails: NewsComponentNavigationImlp): NewsComponentNavigation
}

class NewsComponentNavigationImlp @Inject constructor() : NewsComponentNavigation {
    override fun navigateToEventDetails(fragmentManager: FragmentManager, id: String) {
        fragmentManager.beginTransaction().add(
            R.id.fragmentContainerView,
            EventDetailsFragment.newInstance(id),
            EventDetailsFragment.TAG,
        ).addToBackStack(EventDetailsFragment.TAG).commit()
    }

    override fun navigateToSettings(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction().add(
            R.id.fragmentContainerView,
            FilterFragment.newInstance(),
            FilterFragment.TAG,
        ).addToBackStack(FilterFragment.TAG).commit()
    }
}
