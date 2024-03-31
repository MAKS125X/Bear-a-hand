package com.example.simbirsoftmobile.presentation.screens.search.models

import androidx.fragment.app.Fragment

fun interface SearchQueryListener {
    fun onSearchQueryChanged(query: String)
}

abstract class ViewPagerFragment: Fragment(), SearchQueryListener