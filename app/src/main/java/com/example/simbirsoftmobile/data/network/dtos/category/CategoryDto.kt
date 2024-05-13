package com.example.simbirsoftmobile.data.network.dtos.category

import com.example.simbirsoftmobile.data.local.entities.CategorySettingEntity
import com.example.simbirsoftmobile.data.utils.DataMapper
import com.example.simbirsoftmobile.domain.models.CategoryModel

data class CategoryDto(
    val id: String,
    val name: String,
    val nameEn: String,
    val imageUrl: String,
) : DataMapper<CategoryModel> {
    override fun mapToDomain() =
        CategoryModel(
            this.id,
            this.name,
            this.nameEn,
            this.imageUrl,
            true,
        )
}

fun CategoryDto.toEntity() = CategorySettingEntity(id, name, nameEn, imageUrl)
