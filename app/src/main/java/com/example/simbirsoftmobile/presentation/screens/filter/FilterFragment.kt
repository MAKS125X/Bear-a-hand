package com.example.simbirsoftmobile.presentation.screens.filter

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentFilterBinding
import com.example.simbirsoftmobile.presentation.models.category.CategorySetting
import com.example.simbirsoftmobile.presentation.screens.utils.UiState
import com.example.simbirsoftmobile.repository.CategoryRepository
import com.google.android.material.divider.MaterialDividerItemDecoration
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class FilterFragment : Fragment() {
    private var _binding: FragmentFilterBinding? = null
    private val binding: FragmentFilterBinding
        get() = _binding!!

    private var uiState: UiState<List<CategorySetting>> = UiState.Idle

    private var adapter: CategorySettingAdapter? = null

    private val compositeDisposable = CompositeDisposable()

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

    private fun getNewsListFromBundle(savedInstanceState: Bundle): List<CategorySetting> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState
                .getParcelableArrayList(CATEGORIES_KEY, CategorySetting::class.java)
                ?.toList()
                ?: listOf()
        } else {
            @Suppress("DEPRECATION")
            savedInstanceState.getParcelableArrayList<CategorySetting>(CATEGORIES_KEY)?.toList()
                ?: listOf()
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

        if (uiState is UiState.Success) {
            updateState()
        } else {
            if (savedInstanceState != null) {
                val currentList = getNewsListFromBundle(savedInstanceState)
                if (currentList.isEmpty()) {
                    observeCategories()
                } else {
                    uiState = UiState.Success(currentList)
                    updateState()
                }
            } else {
                observeCategories()
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.accept_filter -> {
                    val currentState = uiState
                    if (currentState is UiState.Success) {
                        CategoryRepository.saveCategorySettings(
                            requireContext(),
                            currentState.data,
                        )
                    }
                    parentFragmentManager.popBackStack()
                }
            }

            true
        }
    }

    private fun observeCategories() {
        uiState = UiState.Loading
        updateState()

        val disposable = CategoryRepository
            .getCategorySettings(requireContext())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                uiState = UiState.Success(it)
                updateState()
            }

        compositeDisposable.add(disposable)
    }

    private fun initAdapter() {
        binding.recyclerView.adapter = adapter

        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        divider.isLastItemDecorated = false
        binding.recyclerView.addItemDecoration(divider)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        const val TAG = "FilterFragment"
        const val CATEGORIES_KEY = "CategoriesList"

        @JvmStatic
        fun newInstance() = FilterFragment()
    }
}
