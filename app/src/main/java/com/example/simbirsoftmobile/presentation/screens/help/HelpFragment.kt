package com.example.simbirsoftmobile.presentation.screens.help

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.simbirsoftmobile.databinding.FragmentHelpBinding
import com.example.simbirsoftmobile.di.appComponent
import com.example.simbirsoftmobile.presentation.base.MviFragment
import javax.inject.Inject

class HelpFragment : MviFragment<HelpState, HelpSideEffect, HelpEvent>() {
    private var _binding: FragmentHelpBinding? = null
    private val binding: FragmentHelpBinding
        get() = _binding!!

    var adapter: CategoryAdapter? = null

    @Inject
    lateinit var factory: HelpViewModel.Factory

    override val viewModel: HelpViewModel by viewModels {
        factory
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun renderState(state: HelpState) {
        with(binding) {
            progressIndicator.isVisible = state.isLoading

            errorTV.isVisible = state.error != null
            state.error?.let {
                errorTV.text = it.asString(requireContext())
            }

            titleTV.isVisible = state.categories.isNotEmpty()
            recyclerView.isVisible = state.categories.isNotEmpty()
            adapter?.submitList(state.categories)
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
    }

    private fun initAdapter() {
        adapter = CategoryAdapter()
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "HelpFragment"

        @JvmStatic
        fun newInstance() = HelpFragment()
    }
}
