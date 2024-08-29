package com.example.data.localWithFetch

import com.example.api.CategoryService
import com.example.api.dtos.category.CategoryDto
import com.example.core.models.category.CategoryModel
import com.example.core.repositories.CategoryRepository
import com.example.data.getRequestFlowDto
import com.example.data.networkBoundResource
import com.example.data.toEntity
import com.example.local.daos.CategoryDao
import com.example.local.entities.toEntity
import com.example.mapper.mapToDomain
import com.example.result.DataError
import com.example.result.DataResult
import com.example.result.Either
import com.example.result.mapDataResult
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
