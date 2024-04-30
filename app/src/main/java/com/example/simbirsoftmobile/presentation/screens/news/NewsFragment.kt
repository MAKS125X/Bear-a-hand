package com.example.simbirsoftmobile.presentation.screens.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentNewsBinding
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.usecases.GetEventsBySettingsUseCase
import com.example.simbirsoftmobile.presentation.models.event.EventShortUi
import com.example.simbirsoftmobile.presentation.models.event.mapToShorUi
import com.example.simbirsoftmobile.presentation.models.settingTest.CategoryPrefsManager.getSettingsEither
import com.example.simbirsoftmobile.presentation.screens.eventDetails.EventDetailsFragment
import com.example.simbirsoftmobile.presentation.screens.filter.FilterFragment
import com.example.simbirsoftmobile.presentation.screens.utils.UiState
import com.example.simbirsoftmobile.presentation.screens.utils.getParcelableListFromBundleByKey
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class NewsFragment : Fragment() {
    private var _binding: FragmentNewsBinding? = null
    private val binding: FragmentNewsBinding
        get() = _binding!!

    private var adapter: NewsAdapter? = null

    private var newsUiState: UiState<List<EventShortUi>> = UiState.Idle

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveListInInstanceState(outState)
    }

    private fun saveListInInstanceState(outState: Bundle) {
        val currentState = newsUiState
        if (currentState is UiState.Success) {
            outState.putParcelableArrayList(LIST_KEY, ArrayList(currentState.data))
        } else {
            newsUiState = UiState.Idle
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

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initToolbar()

        if (newsUiState is UiState.Success) {
            updateUiState()
        } else {
            if (savedInstanceState != null) {
                val currentList =
                    getParcelableListFromBundleByKey<EventShortUi>(savedInstanceState, LIST_KEY)
                if (currentList.isEmpty()) {
                    getNewsListFromFile()
                } else {
                    newsUiState = UiState.Success(currentList)
                    updateUiState()
                }
            } else {
                getNewsListFromFile()
            }
        }
    }

    private fun getNewsListFromFile() {
        val settings =
            getSettingsEither(requireContext())
        when (settings) {
            is Either.Left -> {
                newsUiState = UiState.Error(getString(R.string.empty_category_list_error))
                updateUiState()
            }

            is Either.Right -> {
                val requiredCategoryIds = settings.value
                    .filter { it.isSelected }
                    .map { it.id }

                val exceptionHandler = CoroutineExceptionHandler { _, _ ->
                    newsUiState = UiState.Error(getString(R.string.unexpected_error))
                    updateUiState()
                }

                lifecycleScope.launch(exceptionHandler) {
                    GetEventsBySettingsUseCase()
                        .invoke(*requiredCategoryIds.toTypedArray())
                        .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                        .onEach {
                            newsUiState = UiState.Loading
                            updateUiState()
                        }
                        .collect {
                            newsUiState = when (it) {
                                is Either.Left -> {
                                    UiState.Error(
                                        when (it.value) {
                                            is NetworkError.Api -> it.value.error
                                                ?: getString(R.string.error_occurred_while_receiving_data)

                                            is NetworkError.InvalidParameters -> getString(R.string.unexpected_error)
                                            NetworkError.Timeout -> getString(R.string.timeout_error)
                                            is NetworkError.Unexpected -> getString(R.string.unexpected_error)
                                            is NetworkError.Connection -> getString(R.string.connection_error)
                                        }
                                    )
                                }

                                is Either.Right -> {
                                    UiState.Success(
                                        it.value.map { item -> item.mapToShorUi() }
                                    )
                                }
                            }
                            updateUiState()
                        }
                }
            }
        }
    }

    private fun updateUiState() {
        when (val currentState = newsUiState) {
            is UiState.Error -> {
                with(binding) {
                    progressIndicator.post { progressIndicator.visibility = View.GONE }
                    errorTV.post {
                        errorTV.visibility = View.VISIBLE
                        errorTV.text = currentState.message
                    }
                    newsRecyclerView.post { newsRecyclerView.visibility = View.GONE }
                }
            }

            UiState.Idle -> {}

            UiState.Loading -> {
                with(binding) {
                    progressIndicator.post { progressIndicator.visibility = View.VISIBLE }
                    errorTV.post { errorTV.visibility = View.GONE }
                }
            }

            is UiState.Success -> {
                with(binding) {
                    progressIndicator.post { progressIndicator.visibility = View.GONE }
                    if (currentState.data.isEmpty()) {
                        newsRecyclerView.post { newsRecyclerView.visibility = View.GONE }
                        errorTV.post {
                            errorTV.visibility = View.VISIBLE
                            binding.errorTV.text =
                                getString(R.string.nothing_was_found_based_on_filters)
                        }
                    } else {
                        errorTV.post { errorTV.visibility = View.GONE }
                        newsRecyclerView.post {
                            newsRecyclerView.visibility = View.VISIBLE
                            adapter?.submitList(currentState.data)
                            adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
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
                    newsUiState = UiState.Idle
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
        const val LIST_KEY = "NewsList"

        @JvmStatic
        fun newInstance() = NewsFragment()
    }
}
