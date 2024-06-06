package com.example.simbirsoftmobile.data.network.dtos.event

import com.example.simbirsoftmobile.data.local.entities.EventEntity
import com.example.simbirsoftmobile.data.local.entities.EventPartialEntity
import com.example.simbirsoftmobile.data.utils.DataMapper
import com.example.simbirsoftmobile.domain.models.event.EventModel

data class EventDto(
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
) : DataMapper<EventModel> {
    override fun mapToDomain(): EventModel {
        return EventModel(
            this.id,
            this.name,
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
            false,
        )
    }
}

fun EventDto.toPartialEntity() = EventPartialEntity(
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
    email,
    address,
    organization,
    url,
)

fun EventDto.toEntity(isRead: Boolean) = EventEntity(
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
    email,
    address,
    organization,
    url,
    isRead,
)
