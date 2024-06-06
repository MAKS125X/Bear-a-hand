package com.example.simbirsoftmobile.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class EventPartialEntity(
    val id: String,
    val name: String,
    @ColumnInfo(name = "start_date") val startDate: Long,
    @ColumnInfo(name = "end_date") val endDate: Long,
    val description: String,
    val status: Int,
    val photo: String,
    @ColumnInfo(name = "category_id") val category: String,
    val createAt: Long,
    val phone: String,
    val email: String,
    val address: String,
    val organization: String,
    val url: String,
)

fun EventPartialEntity.toFullEntity(isRead: Boolean) = EventEntity(
    id,
    name,
    startDate,
    endDate,
    description,
    status,
    photo,
    category,
    createAt,
    phone,
    address,
    email,
    organization,
    url,
    isRead,
)
