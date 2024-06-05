package com.example.content_holder.screen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.content_holder.R
import com.example.content_holder.databinding.FragmentContentBinding
import com.example.content_holder.di.ContentDeps
import com.example.content_holder.di.ContentHolderComponentViewModel
import com.example.ui.MviFragment
import dagger.Lazy
import javax.inject.Inject
import kotlin.concurrent.thread

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
    lateinit var deps: ContentDeps

    @Inject
    lateinit var factory: Lazy<ContentViewModel.Factory>

    override val viewModel: ContentViewModel by activityViewModels {
        factory.get()
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
        ViewModelProvider(this).get<ContentHolderComponentViewModel>()
            .newDetailsComponent.inject(this)
        super.onAttach(context)
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
                    deps.contentHolderNavigation.navigateToProfile(binding.contentHolder.id, parentFragmentManager)
                }

                R.id.help_menu -> {
                    deps.contentHolderNavigation.navigateToHelp(binding.contentHolder.id, parentFragmentManager)
                }

                R.id.search_menu -> {
                    deps.contentHolderNavigation.navigateToSearch(binding.contentHolder.id, parentFragmentManager)
                }

                R.id.news_menu -> {
                    deps.contentHolderNavigation.navigateToNews(binding.contentHolder.id, parentFragmentManager)
                }

                R.id.history_menu -> {
                    deps.contentHolderNavigation.navigateToHistory(binding.contentHolder.id, parentFragmentManager)
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
