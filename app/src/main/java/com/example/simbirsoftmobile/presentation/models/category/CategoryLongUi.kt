package com.example.simbirsoftmobile.presentation.models.category

import android.os.Parcelable
import com.example.simbirsoftmobile.domain.models.CategoryModel
import com.example.simbirsoftmobile.presentation.models.utils.UiMapper
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryLongUi(
    val id: String,
    val name: String,
    val nameEn: String,
    val imageUrl: String,
    var isSelected: Boolean,
) : Parcelable, UiMapper<CategoryModel> {
    override fun mapToDomain() = CategoryModel(id, name, nameEn, imageUrl, isSelected)
}

fun CategoryModel.mapToUi(): CategoryLongUi {
    return CategoryLongUi(id, name, nameEn, imageUrl, isSelected)
}
