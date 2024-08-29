package com.example.profile.screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import dagger.Lazy

class TestProfileFragmentFactory(
    private val viewModelFactory: Lazy<ProfileViewModel.Factory>,
    private val viewModel: ProfileViewModel,
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            ProfileFragment::class.java.name -> ProfileFragment().apply {
                this.viewModel = this@TestProfileFragmentFactory.viewModel
                this.factory = this@TestProfileFragmentFactory.viewModelFactory
            }

            else -> super.instantiate(classLoader, className)
        }
    }
}
