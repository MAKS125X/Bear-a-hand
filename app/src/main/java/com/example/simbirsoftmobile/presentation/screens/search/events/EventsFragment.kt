package com.example.simbirsoftmobile.presentation.screens.search.events

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentEventsBinding
import com.example.simbirsoftmobile.presentation.screens.search.SearchFragment
import com.example.simbirsoftmobile.presentation.screens.search.SearchResultAdapter
import com.example.simbirsoftmobile.presentation.screens.search.models.EventSearchResultUI
import com.example.simbirsoftmobile.presentation.screens.search.models.toEventSearchResultUi
import com.example.simbirsoftmobile.presentation.screens.utils.UiState
import com.example.simbirsoftmobile.repository.EventRepository
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class EventsFragment : Fragment() {
    private var _binding: FragmentEventsBinding? = null
    private val binding: FragmentEventsBinding
        get() = _binding!!

    private val adapter: SearchResultAdapter by lazy { SearchResultAdapter() }
    private var uiState: UiState<List<EventSearchResultUI>> = UiState.Idle

    private val currentQuery = MutableSharedFlow<String>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val currentState = uiState
        if (currentState is UiState.Success) {
            outState.putParcelableArrayList(LIST_KEY, ArrayList(currentState.data))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(SearchFragment.QUERY_EVENT_KEY) { _, bundle ->
            val result = bundle.getString(SearchFragment.RESULT_KEY)
            if (result != null) {
                Log.d(TAG, "onCreate: |$result|")
                currentQuery.tryEmit(result)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)

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
        } else {
            uiState = UiState.Idle
            updateUiState()
        }

        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            uiState = UiState.Error(getString(R.string.dat_acquisition_error_occurred))
            updateUiState()
        }

        lifecycleScope.launch(exceptionHandler) {
            currentQuery
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .onEach {
                    uiState = UiState.Loading
                    updateUiState()
                }
                .flowOn(Dispatchers.Main)
                .flatMapLatest { query ->
                    EventRepository.searchEvents(query, requireContext())
                }
                .flowOn(Dispatchers.IO)
                .map { list ->
                    list.map { it.toEventSearchResultUi() }
                }
                .collect {
                    uiState = UiState.Success(it)
                    updateUiState()
                }
        }
    }

    private fun getEventListFromBundle(savedInstanceState: Bundle): List<EventSearchResultUI> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState.getParcelableArrayList(LIST_KEY, EventSearchResultUI::class.java)
                ?.toList()
                ?: listOf()
        } else {
            @Suppress("DEPRECATION")
            savedInstanceState.getParcelableArrayList<EventSearchResultUI>(LIST_KEY)?.toList()
                ?: listOf()
        }

    private fun updateUiState() {
        with(binding) {
            when (val currentState = uiState) {
                is UiState.Error -> {
                    progressIndicator.visibility = View.GONE
                    infoLayout.visibility = View.GONE
                    queryResultLayout.visibility = View.GONE
                    errorTV.visibility = View.VISIBLE
                    errorTV.text = currentState.message
                    searchResultSizeTV.visibility = View.GONE
                }

                UiState.Idle -> {
                    progressIndicator.visibility = View.GONE
                    infoLayout.visibility = View.VISIBLE
                    queryResultLayout.visibility = View.GONE
                    errorTV.visibility = View.GONE
                    searchResultSizeTV.visibility = View.GONE
                }

                UiState.Loading -> {
                    progressIndicator.visibility = View.VISIBLE
                    infoLayout.visibility = View.GONE
                    queryResultLayout.visibility = View.VISIBLE
                    errorTV.visibility = View.GONE
                    searchResultSizeTV.visibility = View.GONE
                }

                is UiState.Success -> {
                    progressIndicator.visibility = View.GONE
                    infoLayout.visibility = View.GONE
                    queryResultLayout.visibility = View.VISIBLE
                    errorTV.visibility = View.GONE
                    searchResultSizeTV.visibility = View.VISIBLE
                    searchResultSizeTV.text =
                        getString(
                            R.string.search_result_events_size,
                            currentState.data.size.toString(),
                        )
                    adapter.submitList(currentState.data)
                }
            }
        }
    }

    private fun initAdapter() {
        binding.recyclerView.adapter = adapter
        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        divider.isLastItemDecorated = false
        binding.recyclerView.addItemDecoration(divider)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "EventsFragment"
        const val LIST_KEY = "EventsListKey"

        @JvmStatic
        fun newInstance() = EventsFragment()
    }
}
