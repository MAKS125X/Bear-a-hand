package com.example.event_details.models

import android.os.Parcelable
import com.example.core.models.event.EventModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventLongUi(
    val id: String,
    val name: String,
    val startDate: Long,
    val endDate: Long,
    val description: String,
    val status: Int,
    val photo: String,
    val category: String,
    val createdAt: Long,
    val phone: String,
    val address: String,
    val email: String,
    val organization: String,
    val url: String,
) : Parcelable

fun EventModel.mapToLongUi(): EventLongUi {
    return EventLongUi(
        id,
        name,
        startDate,
        endDate,
        description,
        status,
        photo,
        category,
        createdAt,
        phone,
        address,
        email,
        organization,
        url,
    )
}
