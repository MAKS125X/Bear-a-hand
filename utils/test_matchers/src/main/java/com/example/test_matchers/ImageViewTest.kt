package com.example.test_matchers

import android.graphics.drawable.VectorDrawable
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher


fun withBitmap(resourceId: Int): Matcher<View> {
    return object : BoundedMatcher<View, ImageView>(ImageView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has bitmap from resource $resourceId")
        }

        override fun matchesSafely(imageView: ImageView): Boolean {
            val expectedBitmap =
                (imageView.context.getDrawable(resourceId) as VectorDrawable).toBitmap()
            val actualBitmap = (imageView.drawable as VectorDrawable).toBitmap()
            return expectedBitmap.sameAs(actualBitmap)
        }
    }
}

fun hasDrawable(): BoundedMatcher<View, ImageView> {
    return object : BoundedMatcher<View, ImageView>(ImageView::class.java) {

        override fun describeTo(description: Description?) {
            description?.appendText("has drawable")
        }

        override fun matchesSafely(item: ImageView?): Boolean {
            return item?.getDrawable() != null;
        }

    }
}