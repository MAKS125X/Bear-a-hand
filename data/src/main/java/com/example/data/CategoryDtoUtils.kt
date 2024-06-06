package com.example.data

import com.example.api.dtos.category.CategoryDto
import com.example.local.entities.CategorySettingEntity

fun CategoryDto.toEntity() = CategorySettingEntity(id, name, nameEn, imageUrl, true)
