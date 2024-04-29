package com.example.simbirsoftmobile.presentation.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.simbirsoftmobile.databinding.FragmentSearchBinding
import com.example.simbirsoftmobile.presentation.screens.search.events.EventsFragment
import com.example.simbirsoftmobile.presentation.screens.search.models.PagerItem
import com.example.simbirsoftmobile.presentation.screens.search.organizations.OrganizationsFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = _binding!!

    private var pagerItems: List<PagerItem>? = null

    private fun sendFragmentApiQuery(query: String) {
        childFragmentManager.setFragmentResult(
            if (binding.fragmentViewPager.currentItem == 0) QUERY_EVENT_KEY else QUERY_ORGANIZATION_KEY,
            bundleOf(RESULT_KEY to query)
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (isVisible && isAdded) {
            outState.putString(RESULT_KEY, binding.searchView.query.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pagerItems =
            listOf(
                PagerItem("По мероприятиям", EventsFragment.newInstance()),
                PagerItem("По НКО", OrganizationsFragment.newInstance()),
            )
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

        if (savedInstanceState != null) {
            val query = savedInstanceState.getString(RESULT_KEY)
            if (query != null) {
                binding.searchView.setQuery(query, true)
            }
        }

        val pagerAdapter = PagerAdapter(childFragmentManager, lifecycle)
        binding.fragmentViewPager.adapter = pagerAdapter

        pagerItems?.let { list ->
            pagerItems?.let {
                pagerAdapter.update(list.map { it.fragment })
                TabLayoutMediator(binding.tabLayout, binding.fragmentViewPager) { tab, position ->
                    tab.text = list[position].title
                }.attach()
            }

            lifecycleScope.launch {
                initSearchObserver()
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .filter { str -> str.isNotBlank() }
                    .distinctUntilChanged()
                    .debounce(500)
                    .flowOn(Dispatchers.IO)
                    .collect {
                        sendFragmentApiQuery(it)
                    }
            }
        }
    }

    private fun initSearchObserver(): StateFlow<String> {
        val stateFlow = MutableStateFlow<String>("")
        with(binding) {
            searchView.setOnSearchClickListener {
                searchView.query?.let { stateFlow.value = it.toString() }
            }

            searchView.setOnQueryTextListener(
                object :
                    SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let { stateFlow.value = it }
                        return true
                    }

                    override fun onQueryTextChange(p0: String?): Boolean {
                        p0?.let { stateFlow.value = it }
                        return true
                    }
                },
            )
        }

        return stateFlow
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SearchFragment"

        const val QUERY_EVENT_KEY = "SearchFragmentEventResult"
        const val QUERY_ORGANIZATION_KEY = "SearchFragmentOrganizationResult"

        const val RESULT_KEY = "SearchFragmentKey"

        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}
