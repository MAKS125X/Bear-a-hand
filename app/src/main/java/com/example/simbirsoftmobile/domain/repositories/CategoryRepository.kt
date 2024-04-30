package com.example.simbirsoftmobile.domain.repositories

import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.CategoryModel
import io.reactivex.rxjava3.core.Observable

interface CategoryRepository {
    fun getCategories(): Observable<Either<NetworkError, List<CategoryModel>>>
}
