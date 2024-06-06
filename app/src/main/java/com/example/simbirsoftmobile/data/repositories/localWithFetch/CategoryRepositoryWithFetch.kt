package com.example.simbirsoftmobile.data.repositories.localWithFetch

import com.example.simbirsoftmobile.data.local.daos.CategoryDao
import com.example.simbirsoftmobile.data.local.entities.toEntity
import com.example.simbirsoftmobile.data.network.api.CategoryService
import com.example.simbirsoftmobile.data.network.dtos.category.CategoryDto
import com.example.simbirsoftmobile.data.network.dtos.category.toEntity
import com.example.simbirsoftmobile.data.utils.getRequestFlowDto
import com.example.simbirsoftmobile.data.utils.mapToDomain
import com.example.simbirsoftmobile.data.utils.networkBoundResource
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.DataResult
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.models.CategoryModel
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository
import com.example.simbirsoftmobile.domain.utils.mapDataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CategoryRepositoryWithFetch @Inject constructor(
    private val categoryService: CategoryService,
    private val dao: CategoryDao,
) : CategoryRepository {
    private var shouldFetch: Boolean = true

    private fun addOrUpdateCategory(category: CategoryDto) {
        val existingCategory = dao.getCategoryById(category.id)
        if (existingCategory != null) {
            val updatedCategory = existingCategory.copy(
                name = category.name,
                nameEn = category.nameEn,
                image = category.imageUrl,
            )
            dao.insertCategory(updatedCategory)
        } else {
            dao.insertCategory(category.toEntity())
        }
    }

    override fun getCategories(): Flow<Either<DataError, DataResult<List<CategoryModel>>>> =
        getCategories(shouldFetch)

    override fun getCategories(shouldFetch: Boolean): Flow<Either<DataError, DataResult<List<CategoryModel>>>> =
        networkBoundResource(
            localQuery = dao::observeCategories,
            apiFetch = {
                getRequestFlowDto {
                    categoryService.getCategories()
                }
            },
            saveFetchResult = { list ->
                for (category in list) {
                    addOrUpdateCategory(category)
                }
                this.shouldFetch = false
            },
            shouldFetch = shouldFetch,
        ).mapDataResult {
            it.mapToDomain()
        }

    override fun updateCategoriesSettings(vararg categories: CategoryModel): Flow<Either<DataError, Unit>> {
        return flow {
            dao.updateCategories(*categories.map { it.toEntity() }.toTypedArray())

            emit(Either.Right(Unit))
        }
    }
}
