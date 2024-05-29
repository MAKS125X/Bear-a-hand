package com.example.simbirsoftmobile.presentation.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import com.example.simbirsoftmobile.databinding.FragmentSearchBinding
import com.example.simbirsoftmobile.presentation.base.MviFragment
import com.example.simbirsoftmobile.presentation.screens.search.events.EventsFragment
import com.example.simbirsoftmobile.presentation.screens.search.models.PagerItem
import com.example.simbirsoftmobile.presentation.screens.search.organizations.OrganizationsFragment
import com.google.android.material.tabs.TabLayoutMediator

class SearchFragment : MviFragment<SearchState, SearchSideEffect, SearchEvent>() {
    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = _binding!!

    override val viewModel: SearchViewModel by activityViewModels()

    override fun renderState(state: SearchState) {
        super.renderState(state)
        with(binding) {
            searchView.setQuery(state.searchQuery, false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initPager()
        initSearchView()
    }

    private fun initPager() {
        val pagerItems =
            listOf(
                PagerItem("По мероприятиям", EventsFragment.newInstance()),
                PagerItem("По НКО", OrganizationsFragment.newInstance()),
            )

        val pagerAdapter = PagerAdapter(childFragmentManager, lifecycle)
        binding.fragmentViewPager.adapter = pagerAdapter

        pagerAdapter.update(pagerItems.map { it.fragment })
        TabLayoutMediator(binding.tabLayout, binding.fragmentViewPager) { tab, position ->
            tab.text = pagerItems[position].title
        }.attach()
    }

    private fun initSearchView() {
        with(binding) {
            searchView.setOnSearchClickListener {
                searchView.query?.let {
                    viewModel.consumeEvent(SearchEvent.Ui.UpdateSearchQuery(it.toString()))
                }
            }

            searchView.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let {
                            viewModel.consumeEvent(SearchEvent.Ui.UpdateSearchQuery(it))
                        }
                        return true
                    }

                    override fun onQueryTextChange(p0: String?): Boolean {
                        p0?.let {
                            viewModel.consumeEvent(SearchEvent.Ui.UpdateSearchQuery(it))
                        }
                        return true
                    }
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SearchFragment"

        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}
