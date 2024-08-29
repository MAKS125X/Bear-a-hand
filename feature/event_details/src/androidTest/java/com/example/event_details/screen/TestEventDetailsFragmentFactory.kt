package com.example.event_details.screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import dagger.Lazy


class TestEventDetailsFragmentFactory(
    private val viewModelFactory: Lazy<EventDetailsViewModel.Factory>,
    private val viewModel: EventDetailsViewModel,
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            EventDetailsFragment::class.java.name -> EventDetailsFragment().apply {
                this.viewModel = this@TestEventDetailsFragmentFactory.viewModel
                this.factory = this@TestEventDetailsFragmentFactory.viewModelFactory
            }

            else -> super.instantiate(classLoader, className)
        }
    }
}
