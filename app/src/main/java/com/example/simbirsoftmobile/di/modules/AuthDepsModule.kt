package com.example.simbirsoftmobile.di.modules

import androidx.fragment.app.FragmentManager
import com.example.auth.di.AuthNavigation
import com.example.content_holder.screen.ContentFragment
import com.example.simbirsoftmobile.R
import dagger.Binds
import dagger.Module
import javax.inject.Inject

@Module
interface AuthDepsModule {
    @Binds
    fun bindAuthNavigation(authNavigation: AuthNavigationImpl): AuthNavigation
}

class AuthNavigationImpl @Inject constructor() : AuthNavigation {
    override fun onSuccessNavigation(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction().replace(
            R.id.fragmentContainerView,
            ContentFragment.newInstance(),
            ContentFragment.TAG,
        ).commit()
    }
}
