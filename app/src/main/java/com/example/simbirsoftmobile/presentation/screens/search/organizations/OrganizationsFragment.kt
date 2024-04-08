package com.example.simbirsoftmobile.presentation.screens.search.organizations

import android.os.Build
import android.os.Bundle
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
import com.example.simbirsoftmobile.presentation.screens.search.models.OrganizationSearchResultUi
import com.example.simbirsoftmobile.presentation.screens.search.models.ViewPagerFragment
import com.example.simbirsoftmobile.presentation.screens.search.models.toOrganizationSearchResultUi
import com.example.simbirsoftmobile.presentation.screens.utils.UiState
import com.example.simbirsoftmobile.repository.EventRepository
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class OrganizationsFragment : ViewPagerFragment() {
    private var _binding: FragmentOrganizationsBinding? = null
    private val binding: FragmentOrganizationsBinding
        get() = _binding!!

    private val adapter: SearchResultAdapter by lazy { SearchResultAdapter() }
    private var uiState: UiState<List<OrganizationSearchResultUi>> = UiState.Idle


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
            val currentList = getOrganizationListFromBundle(savedInstanceState)

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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                currentQuery
                    .onEach {
                        uiState = UiState.Loading
                        updateUiState()
                    }
                    .flowOn(Dispatchers.Main)
                    .flatMapLatest { query ->
                        EventRepository.searchOrganizations(query, requireContext())
                    }
                    .flowOn(Dispatchers.IO)
                    .map { list ->
                        list.map { it.toOrganizationSearchResultUi() }
                    }
                    .collectLatest {
                        if (it.isNotEmpty()) {
                            uiState = UiState.Success(it)
                        }
                        uiState = if (it.isNotEmpty()) {
                            UiState.Success(it)
                        } else {
                            UiState.Error("По данному запросу ничего не найдено")
                        }
                        updateUiState()
                    }
            }
        }
    }

    private fun getOrganizationListFromBundle(savedInstanceState: Bundle): List<OrganizationSearchResultUi> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState.getParcelableArrayList(
                LIST_KEY,
                OrganizationSearchResultUi::class.java
            )
                ?.toList()
                ?: listOf()
        } else {
            @Suppress("DEPRECATION")
            savedInstanceState.getParcelableArrayList<OrganizationSearchResultUi>(LIST_KEY)
                ?.toList()
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
                            R.string.search_result_organizations_size,
                            currentState.data.size.toString(),
                        )
                    adapter.submitList(currentState.data)
                }
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
        currentQuery.tryEmit(query)
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
