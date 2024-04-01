package com.example.simbirsoftmobile.presentation.screens.content

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentContentBinding
import com.example.simbirsoftmobile.presentation.screens.utils.setupWithNavController
import com.example.simbirsoftmobile.repository.EventRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class ContentFragment : Fragment() {
    private var _binding: FragmentContentBinding? = null
    private val binding: FragmentContentBinding
        get() = _binding!!

    private val compositeDisposable = CompositeDisposable()

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        setupBottomNav()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, R.drawable.news_preview.toString())
        Log.d(TAG, R.drawable.news_preview_2.toString())

        val disposable = EventRepository
            .subject
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it <= 0) {
                    binding.bottomNavigationView.removeBadge(R.id.news_menu)
                } else {
                    binding.bottomNavigationView.getOrCreateBadge(R.id.news_menu).number = it
                }
            }
        compositeDisposable.add(disposable)

        if (savedInstanceState == null) {
            setupBottomNav()
            binding.bottomNavigationView.selectedItemId = R.id.help_menu
        }
    }

    private fun setupBottomNav() {
        val navGraphIds = listOf(
            R.navigation.nav_graph_news,
            R.navigation.nav_graph_search,
            R.navigation.nav_graph_help,
            R.navigation.nav_graph_history,
            R.navigation.nav_graph_profile,
        )

        binding.bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = childFragmentManager,
            containerId = binding.contentHolder.id,
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        compositeDisposable.dispose()
    }

    companion object {
        const val TAG = "ContentFragment"

        @JvmStatic
        fun newInstance() =
            ContentFragment()
    }
}