package com.example.simbirsoftmobile.presentation.screens.search.models

import android.os.Parcelable
import com.example.simbirsoftmobile.presentation.models.event.Event
import kotlinx.parcelize.Parcelize

abstract class SearchResultUI(open val name: String) : Parcelable

@Parcelize
data class EventSearchResultUI(
    override val name: String,
) : SearchResultUI(name)

fun Event.toEventSearchResultUi(): EventSearchResultUI =
    EventSearchResultUI(this.title)

@Parcelize
data class OrganizationSearchResultUi(
    override val name: String,
) : SearchResultUI(name)

fun Event.toOrganizationSearchResultUi(): OrganizationSearchResultUi =
    OrganizationSearchResultUi(this.organizerName)
