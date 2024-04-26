package com.example.simbirsoftmobile.presentation.models.category

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategorySetting(
    val category: Category,
    var isSelected: Boolean = true,
) : Parcelable

fun Category.toSettingsModel(): CategorySetting = CategorySetting(this)
