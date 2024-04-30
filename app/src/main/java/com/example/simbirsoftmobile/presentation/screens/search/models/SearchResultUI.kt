package com.example.simbirsoftmobile.presentation.screens.search.models

import android.os.Parcelable
import com.example.simbirsoftmobile.domain.models.EventModel
import kotlinx.parcelize.Parcelize

abstract class SearchResultUI(open val id: String, open val name: String) : Parcelable

@Parcelize
data class EventSearchUi(
    override val id: String,
    override val name: String,
) : Parcelable, SearchResultUI(id, name)

fun EventModel.toEventSearchUi(): EventSearchUi {
    return EventSearchUi(
        id,
        name,
    )
}

@Parcelize
data class OrganizationSearchUi(
    override val id: String,
    override val name: String,
) : Parcelable, SearchResultUI(id, name)

fun EventModel.toOrganizationSearchUi(): OrganizationSearchUi {
    return OrganizationSearchUi(
        id,
        organization,
    )
}
