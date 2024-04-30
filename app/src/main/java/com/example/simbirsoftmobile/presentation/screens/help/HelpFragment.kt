package com.example.simbirsoftmobile.presentation.screens.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentHelpBinding
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.usecases.GetCategoriesUseCase
import com.example.simbirsoftmobile.presentation.models.category.CategoryLongUi
import com.example.simbirsoftmobile.presentation.models.category.mapToUi
import com.example.simbirsoftmobile.presentation.screens.utils.UiState
import com.example.simbirsoftmobile.presentation.screens.utils.getParcelableListFromBundleByKey
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class HelpFragment : Fragment() {
    private var _binding: FragmentHelpBinding? = null
    private val binding: FragmentHelpBinding
        get() = _binding!!

    var adapter: CategoryAdapter? = null
    private var categoriesUiState: UiState<List<CategoryLongUi>> = UiState.Idle

    private val compositeDisposable = CompositeDisposable()

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
            val disposable = GetCategoriesUseCase()
                .invoke()
                .doOnSubscribe {
                    categoriesUiState = UiState.Loading
                    updateUiState()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    categoriesUiState = when (it) {
                        is Either.Left -> {
                            UiState.Error(
                                when (it.value) {
                                    is NetworkError.Api -> it.value.error
                                        ?: getString(R.string.data_acquisition_error_occurred)

                                    is NetworkError.InvalidParameters -> getString(R.string.unexpected_error)
                                    NetworkError.Timeout -> getString(R.string.timeout_error)
                                    is NetworkError.Unexpected -> getString(R.string.unexpected_error)
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

            compositeDisposable.add(disposable)
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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        const val TAG = "HelpFragment"
        const val CATEGORY_LIST_KEY = "categoryListFragmentKey"

        @JvmStatic
        fun newInstance() = HelpFragment()
    }
}
