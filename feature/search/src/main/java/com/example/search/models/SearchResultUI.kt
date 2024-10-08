package com.example.search.models

import android.os.Parcelable
import com.example.core.models.event.EventModel
import com.example.core.models.event.OrganizationModel
import kotlinx.parcelize.Parcelize

abstract class SearchResultUI(open val name: String) : Parcelable

@Parcelize
data class EventSearchUi(
    val id: String,
    override val name: String,
) : Parcelable, SearchResultUI(name)

fun EventModel.toEventSearchUi(): EventSearchUi {
    return EventSearchUi(
        id,
        name,
    )
}

@Parcelize
data class OrganizationSearchUi(
    override val name: String,
) : Parcelable, SearchResultUI(name)

fun OrganizationModel.toOrganizationSearchUi(): OrganizationSearchUi {
    return OrganizationSearchUi(
        name,
    )
}
