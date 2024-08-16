package com.example.test_matchers

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

fun hasItemCount(expectedCount: Int): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("RecyclerView with item count: $expectedCount")
        }

        override fun matchesSafely(recyclerView: RecyclerView): Boolean {
            return recyclerView.adapter?.itemCount == expectedCount
        }
    }
}
