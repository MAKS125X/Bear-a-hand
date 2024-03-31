package com.example.simbirsoftmobile.presentation.screens.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentContentBinding
import com.example.simbirsoftmobile.presentation.screens.utils.setupWithNavController

class ContentFragment : Fragment() {
    private var _binding: FragmentContentBinding? = null
    private val binding: FragmentContentBinding
        get() = _binding!!

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

    companion object {
        const val TAG = "ContentFragment"

        @JvmStatic
        fun newInstance() =
            ContentFragment()
    }
}