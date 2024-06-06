package com.example.simbirsoftmobile.domain.models.event

data class EventModel(
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
    val isRead: Boolean,
)
