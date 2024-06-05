package com.example.help.screen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.help.databinding.FragmentHelpBinding
import com.example.help.di.HelpComponent
import com.example.help.di.HelpComponentViewModel
import com.example.ui.MviFragment
import dagger.Lazy
import javax.inject.Inject

class HelpFragment : MviFragment<HelpState, HelpSideEffect, HelpEvent>() {
    private var _binding: FragmentHelpBinding? = null
    private val binding: FragmentHelpBinding
        get() = _binding!!

    var adapter: CategoryAdapter? = null

    @Inject
    lateinit var factory: Lazy<HelpViewModel.Factory>

    override val viewModel: HelpViewModel by viewModels {
        factory.get()
    }

    override fun onAttach(context: Context) {
        ViewModelProvider(this).get<HelpComponentViewModel>()
            .helpComponent.inject(this)
        super.onAttach(context)
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
