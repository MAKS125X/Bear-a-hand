package com.example.simbirsoftmobile.data.network.api

import com.example.simbirsoftmobile.data.network.dtos.category.CategoryDto
import retrofit2.Response
import retrofit2.http.GET

interface CategoryService {
    @GET("categories")
    suspend fun getCategories(): Response<List<CategoryDto>>
}
