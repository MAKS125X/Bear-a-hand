package com.example.api

import com.example.api.dtos.category.CategoryDto
import retrofit2.Response
import retrofit2.http.GET

interface CategoryService {
    @GET("categories")
    suspend fun getCategories(): Response<List<CategoryDto>>
}
