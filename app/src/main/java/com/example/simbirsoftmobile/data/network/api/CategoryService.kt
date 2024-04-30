package com.example.simbirsoftmobile.data.network.api

import com.example.simbirsoftmobile.data.network.dtos.category.CategoryDto
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

interface CategoryService {
    @GET("categories")
    fun getCategories(): Observable<List<CategoryDto>>
}
