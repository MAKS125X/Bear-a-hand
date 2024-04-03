package com.example.simbirsoftmobile.presentation.screens.help

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentHelpBinding
import com.example.simbirsoftmobile.presentation.models.category.Category
import com.example.simbirsoftmobile.presentation.screens.utils.UiState

class HelpFragment : Fragment() {
    private var _binding: FragmentHelpBinding? = null
    private val binding: FragmentHelpBinding
        get() = _binding!!

    var adapter: CategoryAdapter? = null
    private var categoriesUiState: UiState<List<Category>> = UiState.Idle

    private val dataBroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val currentCategoryList = getArrayListFromIntent(intent)

                if (currentCategoryList.isEmpty()) {
                    categoriesUiState = UiState.Error(getString(R.string.empty_category_list_error))
                    updateUiState()
                } else {
                    categoriesUiState = UiState.Success(currentCategoryList)
                    updateUiState()
                }
            }
        }

    private fun getArrayListFromIntent(intent: Intent) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(
                DownloadCategoriesService.LIST_KEY,
                Category::class.java
            )
                ?: arrayListOf()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra(DownloadCategoriesService.LIST_KEY) ?: arrayListOf()
        }

    private fun updateUiState() {
        with(binding) {
            when (val currentState = categoriesUiState) {
                is UiState.Error -> {
                    progressIndicator.post {
                        progressIndicator.visibility = View.GONE
                    }
                    titleTextView.post {
                        titleTextView.visibility = View.VISIBLE
                        titleTextView.text = currentState.message
                    }
                    recyclerView.post {
                        recyclerView.visibility = View.GONE
                    }
                }

                UiState.Loading -> {
                    progressIndicator.post {
                        progressIndicator.visibility = View.VISIBLE
                    }
                    titleTextView.post {
                        titleTextView.visibility = View.GONE
                    }
                    recyclerView.post {
                        recyclerView.visibility = View.GONE
                    }
                }

                is UiState.Success -> {
                    progressIndicator.post {
                        progressIndicator.visibility = View.GONE
                    }
                    titleTextView.post {
                        titleTextView.visibility = View.VISIBLE
                        titleTextView.text = getString(R.string.select_help_category)
                    }

                    recyclerView.post {
                        recyclerView.visibility = View.VISIBLE
                    }
                    adapter?.submitList(currentState.data)
                    adapter?.notifyDataSetChanged()
                }

                UiState.Idle -> {
                    progressIndicator.post {
                        progressIndicator.visibility = View.GONE
                    }
                    titleTextView.post {
                        titleTextView.visibility = View.GONE
                    }
                    recyclerView.post {
                        recyclerView.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val currentUiState = categoriesUiState
        if (currentUiState is UiState.Success) {
            outState.putParcelableArrayList(CATEGORY_LIST_KEY, ArrayList(currentUiState.data))
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
        registerReceiver()

        if (savedInstanceState != null) {
            val currentCategoryList = getNewsListFromBundle(savedInstanceState)
            if (currentCategoryList.isEmpty()) {
                categoriesUiState = UiState.Error(getString(R.string.empty_category_list_error))
                updateUiState()
            } else {
                categoriesUiState = UiState.Success(currentCategoryList)
                updateUiState()
            }
        } else {
            categoriesUiState = UiState.Loading
            updateUiState()

            requireContext().startService(
                Intent(
                    requireContext(),
                    DownloadCategoriesService::class.java
                )
            )
        }
    }

    private fun registerReceiver() {
        val intentFilter = IntentFilter(DownloadCategoriesService.DOWNLOAD_ACTION)

        val localBroadcastManager = LocalBroadcastManager.getInstance(requireContext())
        localBroadcastManager.registerReceiver(dataBroadcastReceiver, intentFilter)
    }

    private fun getNewsListFromBundle(savedInstanceState: Bundle): List<Category> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState
                .getParcelableArrayList(CATEGORY_LIST_KEY, Category::class.java)?.toList()
                ?: listOf()
        } else {
            @Suppress("DEPRECATION")
            savedInstanceState
                .getParcelableArrayList<Category>(CATEGORY_LIST_KEY)?.toList()
                ?: listOf()
        }

    private fun initAdapter() {
        adapter = CategoryAdapter(requireContext())

        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()

        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(dataBroadcastReceiver)
        requireContext().stopService(
            Intent(
                requireContext(),
                DownloadCategoriesService::class.java,
            )
        )
    }

    companion object {
        const val TAG = "HelpFragment"
        const val CATEGORY_LIST_KEY = "categoryListFragmentKey"

        @JvmStatic
        fun newInstance() = HelpFragment()
    }
}
