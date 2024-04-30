package com.example.simbirsoftmobile.data.network.dtos.category

import com.example.simbirsoftmobile.data.utils.DataMapper
import com.example.simbirsoftmobile.domain.models.CategoryModel

data class CategoryDto(
    val id: String,
    val name: String,
    val nameEn: String,
    val imageUrl: String,
): DataMapper<CategoryModel>{
    override fun mapToDomain(): CategoryModel {
        return CategoryModel(
            this.id,
            this.name,
            this.nameEn,
            this.imageUrl
        )
    }
}

fun CategoryDto.toModel(): CategoryModel {
    return CategoryModel(
        this.id,
        this.name,
        this.nameEn,
        this.imageUrl
    )
}
