package com.example.simbirsoftmobile.data.network.repositories

import com.example.simbirsoftmobile.data.network.api.CategoryService
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.CategoryModel
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CategoryRepositoryNetwork(
    private val categoryService: CategoryService,
) : CategoryRepository {
    override fun getCategories(): Observable<Either<NetworkError, List<CategoryModel>>> {
        return Observable.create { o ->
            categoryService
                .getCategories()
                .timeout(30, TimeUnit.SECONDS) {
                    o.onNext(Either.Left(NetworkError.Timeout))
                }
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        o.onNext(Either.Right(it.map { category -> category.mapToDomain() }))
                    }, {
                        o.onNext(Either.Left(NetworkError.Api(it.message)))
                    }
                )
        }
    }
}
