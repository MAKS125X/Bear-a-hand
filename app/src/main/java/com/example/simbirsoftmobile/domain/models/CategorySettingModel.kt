package com.example.simbirsoftmobile.domain.models

data class CategorySettingModel(
    val id: String,
    val name: String,
    val nameEn: String,
    val imageUrl: String,
    var isSelected: Boolean,
)

fun CategoryModel.toSettingModel(isSelected: Boolean = false): CategorySettingModel {
    return CategorySettingModel(id, name, nameEn, imageUrl, isSelected)
}
