package com.example.search.events

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.search.SearchResultAdapter
import com.example.search.SearchSideEffect
import com.example.search.SearchViewModel
import com.example.search.databinding.FragmentEventsBinding
import com.example.search.di.SearchComponentViewModel
import com.example.ui.MviFragment
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.Lazy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class EventsFragment : MviFragment<EventSearchState, EventSearchSideEffect, EventSearchEvent>() {
    private var _binding: FragmentEventsBinding? = null
    private val binding: FragmentEventsBinding
        get() = _binding!!

    private val adapter: SearchResultAdapter by lazy { SearchResultAdapter() }

    @Inject
    lateinit var factory: Lazy<EventSearchViewModel.Factory>

    override val viewModel: EventSearchViewModel by viewModels {
        factory.get()
    }

    @Inject
    lateinit var searchFactory: SearchViewModel.Factory

    private val sharedSearchViewModel: SearchViewModel by activityViewModels {
        searchFactory
    }

    override fun renderState(state: EventSearchState) {
        with(binding) {
            progressIndicator.isVisible = state.isLoading

            errorTV.isVisible = state.error != null
            state.error?.let {
                errorTV.text = it.asString(requireContext())
            }

            infoLayout.isVisible = state.showInfo

            searchResultSizeTV.isVisible = state.resultInfo != null
            state.resultInfo?.let {
                searchResultSizeTV.text = it.asString(requireContext())
            }

            queryResultLayout.isVisible = state.events.isNotEmpty()
            adapter.submitList(state.events)
        }
    }

    override fun onAttach(context: Context) {
        ViewModelProvider(this).get<SearchComponentViewModel>()
            .searchComponent.inject(this)
        super.onAttach(context)
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

        sharedSearchViewModel.effects
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
            .onEach {
                when (it) {
                    is SearchSideEffect.SearchByQuery -> {
                        viewModel.consumeEvent(EventSearchEvent.Ui.LoadEvents(it.query))
                    }
                }
            }
            .launchIn(lifecycleScope)
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

        @JvmStatic
        fun newInstance() = EventsFragment()
    }
}
