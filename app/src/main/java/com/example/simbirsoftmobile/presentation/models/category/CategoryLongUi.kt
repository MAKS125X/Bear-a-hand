package com.example.simbirsoftmobile.presentation.models.category

import android.os.Parcelable
import com.example.simbirsoftmobile.domain.models.CategoryModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryLongUi(
    val id: String,
    val name: String,
    val nameEn: String,
    val imageUrl: String,
) : Parcelable

fun CategoryModel.mapToUi(): CategoryLongUi {
    return CategoryLongUi(id, name, nameEn, imageUrl)
}
