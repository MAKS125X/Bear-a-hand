package com.example.simbirsoftmobile.presentation.screens.search.organizations

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentOrganizationsBinding
import com.example.simbirsoftmobile.presentation.screens.search.SearchResultAdapter
import com.example.simbirsoftmobile.presentation.screens.search.events.EventsFragment
import com.example.simbirsoftmobile.presentation.screens.search.models.SearchResultUI
import com.example.simbirsoftmobile.presentation.screens.search.models.ViewPagerFragment
import com.example.simbirsoftmobile.presentation.screens.search.models.toSearchResultUi
import com.example.simbirsoftmobile.presentation.screens.utils.UiState
import com.example.simbirsoftmobile.repository.EventRepository
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class OrganizationsFragment : ViewPagerFragment() {
    private var _binding: FragmentOrganizationsBinding? = null
    private val binding: FragmentOrganizationsBinding
        get() = _binding!!

    private val adapter: SearchResultAdapter by lazy { SearchResultAdapter() }
    private var uiState: UiState<List<SearchResultUI>> = UiState.Idle


    private val currentQuery = MutableSharedFlow<String>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val currentState = uiState
        if (currentState is UiState.Success) {
            outState.putParcelableArrayList(LIST_KEY, ArrayList(currentState.data))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOrganizationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        if (savedInstanceState != null) {
            val currentList = getEventListFromBundle(savedInstanceState)

            uiState = UiState.Success(currentList)
            updateUiState()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                currentQuery
                    .onEach { Log.d(EventsFragment.TAG, "onViewCreated: $it") }
                    .flatMapLatest { query ->
                        EventRepository.searchEvent(query, requireContext())
                    }
                    .map { list ->
                        list.map { it.toSearchResultUi() }
                    }
                    .collectLatest {
                        uiState = UiState.Success(it)
                        updateUiState()
                    }
            }
        }
    }

    private fun getEventListFromBundle(savedInstanceState: Bundle): List<SearchResultUI> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState.getParcelableArrayList(LIST_KEY, SearchResultUI::class.java)
                ?.toList()
                ?: listOf()
        } else {
            @Suppress("DEPRECATION")
            savedInstanceState.getParcelableArrayList<SearchResultUI>(LIST_KEY)?.toList()
                ?: listOf()
        }

    private fun updateUiState() {
        when (val currentState = uiState) {
            is UiState.Error -> {}
            UiState.Idle -> {}
            UiState.Loading -> {}
            is UiState.Success -> {
                binding.emptyResultLayout.visibility = View.GONE
                binding.queryResultLayout.visibility = View.VISIBLE
                binding.searchResultSizeTV.text =
                    getString(R.string.search_result_events_size, currentState.data.size.toString())
                adapter.submitList(currentState.data)
            }
        }
    }

    private fun getListOfEventsByQuery(query: String) {
        currentQuery.tryEmit(query)

        lifecycleScope.launch {
            EventRepository.searchEvent(query, requireContext())
                .map { list -> list.map { it.toSearchResultUi() } }
                .collect { results ->
                    uiState = UiState.Success(results)
                    updateUiState()
                }
        }
    }

    private fun initAdapter() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        divider.isLastItemDecorated = false
        binding.recyclerView.addItemDecoration(divider)
    }

    override fun onSearchQueryChanged(query: String) {
        if (context != null) {
            getListOfEventsByQuery(query)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "OrganizationsFragment"
        const val LIST_KEY = "OrganizationsListKey"

        @JvmStatic
        fun newInstance() = OrganizationsFragment()
    }
}
