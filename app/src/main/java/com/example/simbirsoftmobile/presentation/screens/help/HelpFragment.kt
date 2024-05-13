package com.example.simbirsoftmobile.presentation.screens.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentHelpBinding
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.usecases.GetCategoriesUseCase
import com.example.simbirsoftmobile.presentation.models.category.CategoryLongUi
import com.example.simbirsoftmobile.presentation.models.category.mapToUi
import com.example.simbirsoftmobile.presentation.screens.utils.UiState
import com.example.simbirsoftmobile.presentation.screens.utils.getParcelableListFromBundleByKey
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HelpFragment : Fragment() {
    private var _binding: FragmentHelpBinding? = null
    private val binding: FragmentHelpBinding
        get() = _binding!!

    var adapter: CategoryAdapter? = null
    private var categoriesUiState: UiState<List<CategoryLongUi>> = UiState.Idle

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val currentUiState = categoriesUiState
        if (currentUiState is UiState.Success) {
            outState.putParcelableArrayList(CATEGORY_LIST_KEY, ArrayList(currentUiState.data))
        }
    }

    private fun updateUiState() {
        when (val currentState = categoriesUiState) {
            is UiState.Error -> {
                with(binding) {
                    progressIndicator.visibility = View.GONE
                    titleTextView.visibility = View.VISIBLE
                    titleTextView.text = currentState.message
                    recyclerView.visibility = View.GONE
                }
            }

            UiState.Loading -> {
                with(binding) {
                    progressIndicator.visibility = View.VISIBLE
                    titleTextView.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                }
            }

            is UiState.Success -> {
                with(binding) {
                    progressIndicator.visibility = View.GONE
                    titleTextView.visibility = View.VISIBLE
                    titleTextView.text = getString(R.string.select_help_category)

                    recyclerView.visibility = View.VISIBLE
                    adapter?.submitList(currentState.data)
                    adapter?.notifyDataSetChanged()
                }
            }

            UiState.Idle -> {
                with(binding) {
                    progressIndicator.visibility = View.GONE
                    titleTextView.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                }
            }
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

        if (savedInstanceState != null) {
            val currentCategoryList =
                getParcelableListFromBundleByKey<CategoryLongUi>(
                    savedInstanceState,
                    CATEGORY_LIST_KEY,
                )
            if (currentCategoryList.isEmpty()) {
                categoriesUiState =
                    UiState.Error(getString(R.string.data_acquisition_error_occurred))
                updateUiState()
            } else {
                categoriesUiState = UiState.Success(currentCategoryList)
                updateUiState()
            }
        } else {
            observeCategories()
        }
    }

    private fun observeCategories() {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            categoriesUiState = UiState.Error(getString(R.string.unexpected_error))
            updateUiState()
        }

        lifecycleScope.launch(exceptionHandler) {
            GetCategoriesUseCase()
                .invoke()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .onEach {
                    categoriesUiState = UiState.Loading
                    updateUiState()
                }
                .collect {
                    categoriesUiState = when (it) {
                        is Either.Left -> {
                            UiState.Error(
                                when (it.value) {
                                    is DataError.Api -> it.value.error
                                        ?: getString(R.string.error_occurred_while_receiving_data)

                                    is DataError.InvalidParameters -> getString(R.string.unexpected_error)
                                    DataError.Timeout -> getString(R.string.timeout_error)
                                    is DataError.Unexpected -> getString(R.string.unexpected_error)
                                    is DataError.Connection -> getString(R.string.connection_error)
                                    is DataError.NetworkBlock -> getString(R.string.unexpected_error)
                                }
                            )
                        }

                        is Either.Right -> {
                            UiState.Success(
                                it.value.map { item -> item.mapToUi() }
                            )
                        }
                    }
                    updateUiState()
                }
        }
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
        const val CATEGORY_LIST_KEY = "categoryListFragmentKey"

        @JvmStatic
        fun newInstance() = HelpFragment()
    }
}
