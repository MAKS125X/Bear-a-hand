package com.example.simbirsoftmobile.presentation.screens.news

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentNewsBinding
import com.example.simbirsoftmobile.presentation.models.event.Event
import com.example.simbirsoftmobile.presentation.screens.eventDetails.EventDetailsFragment
import com.example.simbirsoftmobile.presentation.screens.filter.FilterFragment
import com.example.simbirsoftmobile.presentation.screens.utils.UiState
import com.example.simbirsoftmobile.repository.CategoryRepository
import com.example.simbirsoftmobile.repository.EventRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class NewsFragment : Fragment() {
    private var _binding: FragmentNewsBinding? = null
    private val binding: FragmentNewsBinding
        get() = _binding!!

    private var adapter: NewsAdapter? = null

    private var newsUiState: UiState<List<Event>> = UiState.Idle
    private val compositeDisposable = CompositeDisposable()

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
                val currentList = getNewsListFromBundle(savedInstanceState)
                if (currentList.isEmpty()) {
                    observeCategories()
                } else {
                    newsUiState = UiState.Success(currentList)
                    updateUiState()
                }
            } else {
                observeCategories()
            }
        }
    }

    private fun getNewsListFromBundle(savedInstanceState: Bundle): List<Event> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState
                .getParcelableArrayList(LIST_KEY, Event::class.java)
                ?.toList()
                ?: listOf()
        } else {
            @Suppress("DEPRECATION")
            savedInstanceState.getParcelableArrayList<Event>(LIST_KEY)?.toList() ?: listOf()
        }

    private fun observeCategories() {
        newsUiState = UiState.Loading
        updateUiState()

        val disposable =
            CategoryRepository.getSelectedCategoriesId(requireContext())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isEmpty()) {
                        newsUiState =
                            UiState.Error(getString(R.string.nothing_was_found_based_on_filters))
                        updateUiState()
                    } else {
                        getNewsListFromFile(it)
                    }
                }

        compositeDisposable.add(disposable)
    }

    private fun getNewsListFromFile(ids: List<Int>) {
        val disposable = EventRepository
            .getAllEventsByCategories(ids, requireContext())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isEmpty()) {
                    newsUiState =
                        UiState.Error(getString(R.string.nothing_was_found_based_on_filters))
                    updateUiState()
                } else {
                    newsUiState = UiState.Success(it)
                    updateUiState()
                }
            }

        compositeDisposable.add(disposable)
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

    private fun moveToEventDetailsFragment(eventId: Int) {
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        const val TAG = "NewsFragment"
        const val LIST_KEY = "NewsList"

        @JvmStatic
        fun newInstance() = NewsFragment()
    }
}
