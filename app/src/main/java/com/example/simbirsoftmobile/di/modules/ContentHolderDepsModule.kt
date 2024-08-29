package com.example.simbirsoftmobile.di.modules

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.example.content_holder.di.ContentHolderNavigation
import com.example.event_details.screen.EventDetailsFragment
import com.example.help.screen.HelpFragment
import com.example.history.screen.HistoryFragment
import com.example.news.screen.NewsFragment
import com.example.profile.screen.ProfileFragment
import com.example.search.SearchFragment
import dagger.Binds
import dagger.Module
import javax.inject.Inject

@Module
interface ContentHolderDepsModule {
    @Binds
    fun bindContentHolderNavigation(contentHolderNavigationImpl: ContentHolderNavigationImpl): ContentHolderNavigation
}

class ContentHolderNavigationImpl @Inject constructor() : ContentHolderNavigation {
    override fun navigateToSearch(@IdRes containerId: Int, fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction().replace(
            containerId,
            SearchFragment.newInstance(),
            SearchFragment.TAG,
        ).commit()
    }

    override fun navigateToProfile(@IdRes containerId: Int, fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction().replace(
            containerId,
            ProfileFragment.newInstance(),
            ProfileFragment.TAG,
        ).commit()
    }

    override fun navigateToHelp(@IdRes containerId: Int, fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction().replace(
            containerId,
            HelpFragment.newInstance(),
            HelpFragment.TAG,
        ).commit()
    }

    override fun navigateToNews(@IdRes containerId: Int, fragmentManager: FragmentManager) {
        val currentFragment =
            fragmentManager.findFragmentById(containerId)
        if (currentFragment !is EventDetailsFragment) {
            fragmentManager.beginTransaction().replace(
                containerId,
                NewsFragment.newInstance(),
                NewsFragment.TAG,
            ).commit()
        }
    }

    override fun navigateToHistory(@IdRes containerId: Int, fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction().replace(
            containerId,
            HistoryFragment.newInstance(),
            HistoryFragment.TAG,
        ).commit()
    }
}
