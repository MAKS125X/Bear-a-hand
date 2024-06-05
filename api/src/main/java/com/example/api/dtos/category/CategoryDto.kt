package com.example.api.dtos.category

import com.example.core.models.category.CategoryModel
import com.example.mapper.DataMapper

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
