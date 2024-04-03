package com.example.simbirsoftmobile.presentation.models.event

import android.os.Parcelable
import kotlinx.datetime.LocalDate
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: Int,
    val organizerName: String,
    val categoryList: List<Int>,
    val address: String,
    val phoneNumbers: List<String>,
    val email: String,
    val siteUrl: String,
    val dateStart: @RawValue LocalDate,
    val dateEnd: @RawValue LocalDate,
) : Parcelable
