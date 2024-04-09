package com.example.simbirsoftmobile.presentation.screens.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentContentBinding
import com.example.simbirsoftmobile.presentation.screens.eventDetails.EventDetailsFragment
import com.example.simbirsoftmobile.presentation.screens.help.HelpFragment
import com.example.simbirsoftmobile.presentation.screens.news.NewsFragment
import com.example.simbirsoftmobile.presentation.screens.profile.ProfileFragment
import com.example.simbirsoftmobile.presentation.screens.search.SearchFragment
import com.example.simbirsoftmobile.repository.EventRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class ContentFragment : Fragment() {
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

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initBottomNavigation()

        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.help_menu
        }

        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            binding.bottomNavigationView.removeBadge(R.id.news_menu)
        }

        lifecycleScope.launch(exceptionHandler) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                EventRepository.notificationFlow
                    .collect {
                        with(binding) {
                            if (it <= 0) {
                                bottomNavigationView.removeBadge(R.id.news_menu)
                            } else {
                                bottomNavigationView.getOrCreateBadge(R.id.news_menu).number = it
                            }
                        }
                    }
            }
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
