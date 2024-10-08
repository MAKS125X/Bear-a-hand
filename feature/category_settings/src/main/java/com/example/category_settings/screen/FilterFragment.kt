package com.example.category_settings.screen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.category_settings.R
import com.example.category_settings.databinding.FragmentFilterBinding
import com.example.category_settings.di.FilterComponentViewModel
import com.example.ui.MviFragment
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.Lazy
import javax.inject.Inject

class FilterFragment : MviFragment<FilterState, FilterSideEffect, FilterEvent>() {
    private var _binding: FragmentFilterBinding? = null
    private val binding: FragmentFilterBinding
        get() = _binding!!

    private val adapter: CategorySettingAdapter by lazy {
        CategorySettingAdapter {
            onCheckBoxClick(it)
        }
    }

    @Inject
    lateinit var factory: Lazy<FilterViewModel.Factory>

    override val viewModel: FilterViewModel by viewModels {
        factory.get()
    }

    override val initialEvent: FilterEvent = FilterEvent.Internal.LoadCategories

    override fun renderState(state: FilterState) {
        with(binding) {
            progressIndicator.isVisible = state.isLoading

            errorTV.isVisible = state.error != null
            state.error?.let {
                errorTV.text = state.error.asString(requireContext())
            }

            recyclerView.isVisible = state.categories.isNotEmpty()

            adapter.submitList(state.categories)
        }
    }

    override fun handleSideEffects(effect: FilterSideEffect) {
        when (effect) {
            is FilterSideEffect.ErrorChangesUpdate -> {
                Toast.makeText(
                    requireContext(),
                    effect.error.asString(requireContext()),
                    Toast.LENGTH_SHORT
                ).show()
            }

            FilterSideEffect.SuccessChangesUpdate -> {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun onCheckBoxClick(id: String) {
        viewModel.consumeEvent(FilterEvent.Ui.UpdateSelectedById(id))
    }

    override fun onAttach(context: Context) {
        ViewModelProvider(this).get<FilterComponentViewModel>()
            .newDetailsComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.accept_filter -> {
                    viewModel.consumeEvent(FilterEvent.Ui.AcceptChanges)
                }
            }

            true
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
        const val TAG = "FilterFragment"

        @JvmStatic
        fun newInstance() = FilterFragment()
    }
}
