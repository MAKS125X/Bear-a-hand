package com.example.category_settings.model

import android.os.Parcelable
import com.example.core.models.category.CategoryModel
import com.example.mapper.DataMapper
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingUi(
    val id: String,
    val name: String,
    val nameEn: String,
    val imageUrl: String,
    var isSelected: Boolean,
) : Parcelable, DataMapper<CategoryModel> {
    override fun mapToDomain(): CategoryModel =
        CategoryModel(
            id,
            name,
            nameEn,
            imageUrl,
            isSelected
        )
}

fun CategoryModel.mapToUi(): SettingUi {
    return SettingUi(id, name, nameEn, imageUrl, isSelected)
}
