package com.example.simbirsoftmobile.presentation.screens.content

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentContentBinding
import com.example.simbirsoftmobile.di.appComponent
import com.example.simbirsoftmobile.presentation.base.MviFragment
import com.example.simbirsoftmobile.presentation.screens.eventDetails.EventDetailsFragment
import com.example.simbirsoftmobile.presentation.screens.help.HelpFragment
import com.example.simbirsoftmobile.presentation.screens.history.HistoryFragment
import com.example.simbirsoftmobile.presentation.screens.news.NewsFragment
import com.example.simbirsoftmobile.presentation.screens.profile.ProfileFragment
import com.example.simbirsoftmobile.presentation.screens.search.SearchFragment
import javax.inject.Inject

class ContentFragment : MviFragment<ContentState, ContentSideEffect, ContentEvent>() {
    private var _binding: FragmentContentBinding? = null
    private val binding: FragmentContentBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Inject
    lateinit var factory: ContentViewModel.Factory

    override val viewModel: ContentViewModel by activityViewModels {
        factory
    }

    override fun renderState(state: ContentState) {
        with(binding) {
            if (state.badge <= 0) {
                bottomNavigationView.removeBadge(R.id.news_menu)
            } else {
                bottomNavigationView.getOrCreateBadge(R.id.news_menu).number = state.badge
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initBottomNavigation()

        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.help_menu
        }
    }

    private fun initBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.profile_menu -> {
                    parentFragmentManager.beginTransaction().replace(
                        binding.contentHolder.id,
                        ProfileFragment.newInstance(),
                        ProfileFragment.TAG,
                    ).commit()
                }

                R.id.help_menu -> {
                    parentFragmentManager.beginTransaction().replace(
                        binding.contentHolder.id,
                        HelpFragment.newInstance(),
                        HelpFragment.TAG,
                    ).commit()
                }

                R.id.search_menu -> {
                    parentFragmentManager.beginTransaction().replace(
                        binding.contentHolder.id,
                        SearchFragment.newInstance(),
                        SearchFragment.TAG,
                    ).commit()
                }

                R.id.news_menu -> {
                    val currentFragment =
                        parentFragmentManager.findFragmentById(R.id.fragmentHolder)
                    if (currentFragment !is EventDetailsFragment) {
                        parentFragmentManager.beginTransaction().replace(
                            binding.contentHolder.id,
                            NewsFragment.newInstance(),
                            NewsFragment.TAG,
                        ).commit()
                    }
                }

                R.id.history_menu -> {
                    parentFragmentManager.beginTransaction().replace(
                        binding.contentHolder.id,
                        HistoryFragment.newInstance(),
                        HistoryFragment.TAG,
                    ).commit()
                }

                else -> {
                    val toastMassage = it.title
                    Toast.makeText(requireContext(), toastMassage, Toast.LENGTH_SHORT).show()
                }
            }

            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ContentFragment"

        @JvmStatic
        fun newInstance() = ContentFragment()
    }
}
