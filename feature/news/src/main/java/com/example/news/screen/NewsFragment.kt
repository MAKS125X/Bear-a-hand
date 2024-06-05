package com.example.news.screen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import com.example.news.databinding.FragmentNewsBinding
import com.example.news.di.NewsComponentViewModel
import com.example.news.di.NewsDeps
import com.example.ui.MviFragment
import dagger.Lazy
import javax.inject.Inject

class NewsFragment : MviFragment<NewsState, NewsSideEffect, NewsEvent>() {
    private var _binding: FragmentNewsBinding? = null
    private val binding: FragmentNewsBinding
        get() = _binding!!

    private var adapter: NewsAdapter? = null

    @Inject
    lateinit var newsDeps: NewsDeps

    @Inject
    lateinit var factory: Lazy<NewsViewModel.Factory>

    override val viewModel: NewsViewModel by viewModels {
        factory.get()
    }

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

    override fun onAttach(context: Context) {
        ViewModelProvider(this).get<NewsComponentViewModel>()
            .newDetailsComponent.inject(this)
        super.onAttach(context)
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
        newsDeps.moveToEventDetails.moveToEventDetails(parentFragmentManager, eventId)
    }

    private fun initToolbar() {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.open_filter -> {
//                    newsDeps.moveToSettings.moveToSettings()
//                    parentFragmentManager.beginTransaction().replace(
//                        R.id.contentHolder,
//                        FilterFragment.newInstance(),
//                        FilterFragment.TAG,
//                    ).addToBackStack(FilterFragment.TAG).commit()
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
