package com.example.simbirsoftmobile.presentation.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.simbirsoftmobile.databinding.FragmentSearchBinding
import com.example.simbirsoftmobile.presentation.screens.search.events.EventsFragment
import com.example.simbirsoftmobile.presentation.screens.search.models.PagerItem
import com.example.simbirsoftmobile.presentation.screens.search.organizations.OrganizationsFragment
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = _binding!!

    private val compositeDisposable = CompositeDisposable()

    private var pagerItems: List<PagerItem>? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (isVisible && isAdded) {
            outState.putString(SEARCH_RESULT_KEY, binding.searchView.query.toString())
        }
    }

    private val callback = object :
        ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            pagerItems?.let { list ->
                list[position].fragment.onSearchQueryChanged(binding.searchView.query.toString())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            val query = savedInstanceState.getString(SEARCH_RESULT_KEY, "")
            binding.searchView.setQuery(query, false)
        }

        pagerItems =
            listOf(
                PagerItem("По мероприятиям", EventsFragment.newInstance()),
                PagerItem("По НКО", OrganizationsFragment.newInstance()),
            )

        val pagerAdapter = PagerAdapter(this)
        binding.fragmentViewPager.adapter = pagerAdapter

        pagerItems?.let { list ->
            pagerAdapter.update(list.map { it.fragment })
            TabLayoutMediator(binding.tabLayout, binding.fragmentViewPager) { tab, position ->
                tab.text = list[position].title
            }.attach()

            binding.fragmentViewPager.registerOnPageChangeCallback(callback)

            val dispose = initSearchObserver()
                .filter { str -> str.isNotEmpty() }
                .distinctUntilChanged()
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe { query ->
                    list[binding.fragmentViewPager.currentItem]
                        .fragment
                        .onSearchQueryChanged(query)
                }

            compositeDisposable.add(dispose)
        }
    }

    private fun initSearchObserver() =
        Observable.create<String> { emitter ->
            binding.searchView.setOnQueryTextListener(
                object :
                    SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let { emitter.onNext(it) }
                        return true
                    }

                    override fun onQueryTextChange(p0: String?): Boolean {
                        p0?.let { emitter.onNext(it) }
                        return true
                    }
                },
            )
        }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.fragmentViewPager.unregisterOnPageChangeCallback(callback)
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        const val TAG = "SearchFragment"
        const val SEARCH_RESULT_KEY = "SearchFragmentKey"

        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}
