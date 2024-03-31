package com.example.simbirsoftmobile.presentation.screens.search

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.simbirsoftmobile.presentation.screens.search.models.ViewPagerFragment

class PagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {
    private val fragments: MutableList<ViewPagerFragment> = mutableListOf()

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]



    fun update(fragments: List<ViewPagerFragment>) {
        this.fragments.clear()
        this.fragments.addAll(fragments)
        notifyDataSetChanged()
    }
}
