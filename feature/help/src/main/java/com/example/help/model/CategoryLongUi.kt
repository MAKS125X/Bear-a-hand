package com.example.help.model

import android.os.Parcelable
import com.example.core.models.category.CategoryModel

import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryLongUi(
    val id: String,
    val name: String,
    val nameEn: String,
    val imageUrl: String,
    var isSelected: Boolean,
) : Parcelable

fun CategoryModel.mapToUi(): CategoryLongUi {
    return CategoryLongUi(id, name, nameEn, imageUrl, isSelected)
}
