package com.example.simbirsoftmobile.presentation.screens.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentNewsBinding
import com.example.simbirsoftmobile.presentation.base.MviFragment
import com.example.simbirsoftmobile.presentation.screens.eventDetails.EventDetailsFragment
import com.example.simbirsoftmobile.presentation.screens.filter.FilterFragment

class NewsFragment : MviFragment<NewsState, NewsSideEffect, NewsEvent>() {
    private var _binding: FragmentNewsBinding? = null
    private val binding: FragmentNewsBinding
        get() = _binding!!

    private var adapter: NewsAdapter? = null

    override val viewModel: NewsViewModel by viewModels()

    override fun renderState(state: NewsState) {
        with(binding) {
            progressIndicator.isVisible = state.isLoading

            newsRecyclerView.isVisible = state.news.isNotEmpty()
            adapter?.submitList(state.news)

            errorTV.isVisible = state.error != null
            state.error?.let {
                errorTV.text = it.asString(requireContext())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initToolbar()
    }

    private fun initAdapter() {
        adapter = NewsAdapter(this::moveToEventDetailsFragment, requireContext())
        adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.newsRecyclerView.adapter = adapter
    }

    private fun moveToEventDetailsFragment(eventId: String) {
        parentFragmentManager.beginTransaction().replace(
            R.id.contentHolder,
            EventDetailsFragment.newInstance(eventId),
            EventDetailsFragment.TAG,
        ).addToBackStack(EventDetailsFragment.TAG).commit()
    }

    private fun initToolbar() {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.open_filter -> {
                    parentFragmentManager.beginTransaction().replace(
                        R.id.contentHolder,
                        FilterFragment.newInstance(),
                        FilterFragment.TAG,
                    ).addToBackStack(FilterFragment.TAG).commit()
                }
            }
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
    }

    companion object {
        const val TAG = "NewsFragment"

        @JvmStatic
        fun newInstance() = NewsFragment()
    }
}
