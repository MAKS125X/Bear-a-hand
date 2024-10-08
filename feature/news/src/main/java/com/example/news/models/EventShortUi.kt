package com.example.news.models

import android.os.Parcelable
import com.example.core.models.event.EventModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventShortUi(
    val id: String,
    val name: String,
    val startDate: Long,
    val endDate: Long,
    val description: String,
    val status: Int,
    val photo: String,
    val category: String,
    val createdAt: Long,
) : Parcelable

fun EventModel.mapToShorUi(): EventShortUi {
    return EventShortUi(
        id,
        name,
        startDate,
        endDate,
        description,
        status,
        photo,
        category,
        createdAt,
    )
}
