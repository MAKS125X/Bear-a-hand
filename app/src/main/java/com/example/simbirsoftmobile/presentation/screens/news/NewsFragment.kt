package com.example.simbirsoftmobile.presentation.screens.news

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentNewsBinding
import com.example.simbirsoftmobile.presentation.models.event.Event
import com.example.simbirsoftmobile.presentation.screens.eventDetails.EventDetailsFragment
import com.example.simbirsoftmobile.presentation.screens.filter.FilterFragment
import com.example.simbirsoftmobile.presentation.screens.utils.UiState
import com.example.simbirsoftmobile.repository.CategoryRepository
import com.example.simbirsoftmobile.repository.EventRepository
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class NewsFragment : Fragment() {
    private var _binding: FragmentNewsBinding? = null
    private val binding: FragmentNewsBinding
        get() = _binding!!

    private var adapter: NewsAdapter? = null

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var downloadTask: Future<*>? = null
    private var newsUiState: UiState<List<Event>> = UiState.Idle

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveListInInstanceState(outState)
    }

    private fun saveListInInstanceState(outState: Bundle) {
        val currentState = newsUiState
        if (currentState is UiState.Success) {
            outState.putParcelableArrayList(LIST_KEY, ArrayList(currentState.data))
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
                val currentList = getNewsListFromBundle(savedInstanceState)

                newsUiState = UiState.Success(currentList)
                updateUiState()
            } else {
                getNewsListFromFile()
            }
        }
    }

    private fun getNewsListFromBundle(savedInstanceState: Bundle): List<Event> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState.getParcelableArrayList(LIST_KEY, Event::class.java)?.toList()
                ?: listOf()
        } else {
            @Suppress("DEPRECATION")
            savedInstanceState.getParcelableArrayList<Event>(LIST_KEY)?.toList() ?: listOf()
        }

    private fun getNewsListFromFile() {
        newsUiState = UiState.Loading
        updateUiState()

        val loadList = Runnable {
            val requiredCategories =
                CategoryRepository.getSelectedCategoriesId(requireContext())

            if (requiredCategories.isEmpty()) {
                newsUiState =
                    UiState.Error(getString(R.string.nothing_was_found_based_on_filters))
                updateUiState()
            } else {
                val events =
                    EventRepository.getAllEventsByCategories(
                        requiredCategories,
                        requireContext(),
                    )

                newsUiState = UiState.Success(events)
                updateUiState()
            }
        }

        downloadTask = executor.submit(loadList)
    }

    private fun updateUiState() {
        with(binding) {
            when (val currentState = newsUiState) {
                is UiState.Error -> {
                    progressIndicator.post { progressIndicator.visibility = View.GONE }
                    errorTV.post {
                        errorTV.visibility = View.VISIBLE
                        errorTV.text = currentState.message
                    }
                    recyclerView.post { recyclerView.visibility = View.GONE }
                }

                UiState.Idle -> {}

                UiState.Loading -> {
                    progressIndicator.post { progressIndicator.visibility = View.VISIBLE }
                    errorTV.post { errorTV.visibility = View.GONE }
                }

                is UiState.Success -> {
                    progressIndicator.post { progressIndicator.visibility = View.GONE }
                    if (currentState.data.isEmpty()) {
                        recyclerView.post { recyclerView.visibility = View.GONE }
                        errorTV.post {
                            errorTV.visibility = View.VISIBLE
                            binding.errorTV.text =
                                getString(R.string.nothing_was_found_based_on_filters)
                        }
                    } else {
                        errorTV.post { errorTV.visibility = View.GONE }
                        recyclerView.post {
                            recyclerView.visibility = View.VISIBLE
                            adapter?.submitList(currentState.data)
                        }
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        adapter = NewsAdapter(this::moveToEventDetailsFragment, requireContext())

        binding.recyclerView.adapter = adapter
    }

    private fun moveToEventDetailsFragment(eventId: Int) {
        parentFragmentManager.beginTransaction().replace(
            R.id.fragmentHolder,
            EventDetailsFragment.newInstance(eventId),
            EventDetailsFragment.TAG,
        ).addToBackStack(EventDetailsFragment.TAG).commit()
    }

    private fun initToolbar() {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.open_filter -> {
                    parentFragmentManager.beginTransaction().replace(
                        R.id.fragmentHolder,
                        FilterFragment.newInstance(),
                        FilterFragment.TAG,
                    ).addToBackStack(FilterFragment.TAG).commit()
                    newsUiState = UiState.Idle
                }
            }
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        adapter = null
        downloadTask?.cancel(true)
    }

    companion object {
        const val TAG = "NewsFragment"
        const val LIST_KEY = "NewsList"

        @JvmStatic
        fun newInstance() = NewsFragment()
    }
}
