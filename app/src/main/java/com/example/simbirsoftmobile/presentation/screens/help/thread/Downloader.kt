package com.example.simbirsoftmobile.presentation.screens.help.thread

import android.content.Context
import com.example.simbirsoftmobile.presentation.models.category.Category
import com.example.simbirsoftmobile.presentation.screens.utils.TypedListener
import com.example.simbirsoftmobile.repository.CategoryRepository
import java.lang.ref.WeakReference

class Downloader(
    context: Context,
    callback: TypedListener<List<Category>>,
) {
    private val callbackReference: WeakReference<TypedListener<List<Category>>> =
        WeakReference(callback)
    private val contextReference: WeakReference<Context> = WeakReference(context)

    private var thread = Thread {
        val ref = contextReference.get()
        if (ref != null) {
            val categories =
                CategoryRepository.getCategories(ref)
            if (categories.isEmpty()) {
                callbackReference.get()
                    ?.invoke(listOf())
            } else {
                callbackReference.get()
                    ?.invoke(categories)
            }
        }
    }

    fun load() {
        thread.start()
    }

    fun stop() {
        thread.interrupt()
    }
}
