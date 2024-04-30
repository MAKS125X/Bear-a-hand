package com.example.simbirsoftmobile.presentation.models.category

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryShortUi(
    val id: String,
    val name: String,
) : Parcelable
