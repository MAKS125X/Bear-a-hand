package com.example.data

import com.example.api.dtos.event.EventDto
import com.example.local.entities.EventEntity
import com.example.local.entities.EventPartialEntity

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
