package com.example.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.models.category.CategoryModel
import com.example.mapper.DataMapper

@Entity(tableName = "category")
data class CategorySettingEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo("name_en") val nameEn: String,
    val image: String,
    @ColumnInfo("is_selected") val isSelected: Boolean = true,
) : DataMapper<CategoryModel> {
    override fun mapToDomain() = CategoryModel(id, name, nameEn, image, isSelected)
}

fun CategoryModel.toEntity() =
    CategorySettingEntity(
        id,
        name,
        nameEn,
        imageUrl,
        isSelected,
    )
