package com.example.simbirsoftmobile.presentation.screens.search.models

import android.os.Parcelable
import com.example.simbirsoftmobile.presentation.models.event.Event
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchResultUI(
    val name: String,
): Parcelable

fun Event.toSearchResultUi(): SearchResultUI = SearchResultUI(this.title)