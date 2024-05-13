package com.example.simbirsoftmobile.presentation.screens.filter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentFilterBinding
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.usecases.GetCategoriesUseCase
import com.example.simbirsoftmobile.domain.usecases.UpdateCategoriesSettingsUseCase
import com.example.simbirsoftmobile.presentation.models.category.CategoryLongUi
import com.example.simbirsoftmobile.presentation.models.category.mapToUi
import com.example.simbirsoftmobile.presentation.models.utils.mapToDomain
import com.example.simbirsoftmobile.presentation.screens.utils.UiState
import com.example.simbirsoftmobile.presentation.screens.utils.getParcelableListFromBundleByKey
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FilterFragment : Fragment() {
    private var _binding: FragmentFilterBinding? = null
    private val binding: FragmentFilterBinding
        get() = _binding!!

    private var uiState: UiState<List<CategoryLongUi>> = UiState.Idle

    private var adapter: CategorySettingAdapter? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveCategoriesInstanceState(outState)
    }

    private fun saveCategoriesInstanceState(outState: Bundle) {
        val currentState = uiState
        if (currentState is UiState.Success) {
            outState.putParcelableArrayList(CATEGORIES_KEY, ArrayList(currentState.data))
        } else {
            uiState = UiState.Idle
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        adapter = CategorySettingAdapter(context = context)
    }

    private fun updateState() {
        when (val currentState = uiState) {
            is UiState.Error -> {
                with(binding) {
                    recyclerView.visibility = View.GONE
                    progressIndicator.visibility = View.GONE
                    errorTV.visibility = View.VISIBLE
                    errorTV.text = currentState.message
                }
            }

            UiState.Idle -> {}

            UiState.Loading -> {
                with(binding) {
                    recyclerView.visibility = View.GONE
                    progressIndicator.visibility = View.VISIBLE
                    errorTV.visibility = View.GONE
                }
            }

            is UiState.Success -> {
                with(binding) {
                    recyclerView.visibility = View.VISIBLE
                    progressIndicator.visibility = View.GONE
                    errorTV.visibility = View.GONE
                    adapter?.submitList(currentState.data)
                    adapter?.notifyDataSetChanged()
                }
            }
        }
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

        when {
            uiState is UiState.Success -> updateState()
            savedInstanceState != null -> {
                val currentList =
                    getParcelableListFromBundleByKey<CategoryLongUi>(
                        savedInstanceState,
                        CATEGORIES_KEY,
                    )
                if (currentList.isEmpty()) {
                    observeCategories()
                } else {
                    uiState = UiState.Success(currentList)
                    updateState()
                }
            }

            else -> observeCategories()
        }

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.accept_filter -> {
                    val currentState = uiState
                    if (currentState is UiState.Success) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                UpdateCategoriesSettingsUseCase().invoke(
                                    *currentState.data.mapToDomain().toTypedArray()
                                )
                            }
                        }
                    }
                    parentFragmentManager.popBackStack()
                }
            }

            true
        }
    }

    private fun observeCategories() {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            uiState = UiState.Error(getString(R.string.unexpected_error))
            updateState()
        }

        lifecycleScope.launch(exceptionHandler) {
            GetCategoriesUseCase()
                .invoke()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .onEach {
                    uiState = UiState.Loading
                    updateState()
                }
                .collect {
                    uiState = when (it) {
                        is Either.Left -> {
                            UiState.Error(
                                when (it.value) {
                                    is DataError.Api -> it.value.error
                                        ?: getString(R.string.unexpected_error)

                                    is DataError.InvalidParameters -> getString(R.string.unexpected_error)
                                    DataError.Timeout -> getString(R.string.timeout_error)
                                    is DataError.Unexpected -> getString(R.string.unexpected_error)
                                    is DataError.Connection -> getString(R.string.connection_error)
                                    is DataError.NetworkBlock -> getString(R.string.unexpected_error)
                                }
                            )
                        }

                        is Either.Right -> {
                            UiState.Success(it.value.map { model -> model.mapToUi() })
                        }
                    }

                    updateState()
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
        const val TAG = "FilterFragment"
        const val CATEGORIES_KEY = "CategoriesList"

        @JvmStatic
        fun newInstance() = FilterFragment()
    }
}
