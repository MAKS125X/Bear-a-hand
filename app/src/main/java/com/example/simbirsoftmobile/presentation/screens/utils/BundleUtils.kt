package com.example.simbirsoftmobile.presentation.screens.utils

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

inline fun <reified T : Parcelable> getParcelableListFromBundleByKey(
    savedInstanceState: Bundle,
    key: String,
): List<T> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        savedInstanceState
            .getParcelableArrayList(key, T::class.java)
            ?.toList()
            ?: listOf()
    } else {
        @Suppress("DEPRECATION")
        savedInstanceState.getParcelableArrayList<T>(key)?.toList()
            ?: listOf()
    }

inline fun <reified T : Parcelable> getSingleParcelableFromBundleByKey(
    savedInstanceState: Bundle,
    key: String,
): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        savedInstanceState.getParcelable(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        savedInstanceState.getParcelable(key)
    }
