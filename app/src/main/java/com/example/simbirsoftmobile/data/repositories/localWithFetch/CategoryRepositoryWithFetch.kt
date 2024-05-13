package com.example.simbirsoftmobile.data.repositories.localWithFetch

import  com.example.simbirsoftmobile.data.local.TransactionProvider
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

class CategoryRepositoryWithFetch(
    private val categoryService: CategoryService,
    private val dao: CategoryDao,
    private val transactionProvider: TransactionProvider,
) : CategoryRepository {
    private var shouldFetch: Boolean = true

    private fun addOrUpdateCategory(category: CategoryDto) {
        val existingCategory = dao.getCategoryById(category.id)
        if (existingCategory != null) {
            val updatedCategory = existingCategory.copy(
                name = category.name,
                nameEn = category.nameEn,
                image = category.imageUrl
            )
            dao.insertCategory(updatedCategory)
        } else {
            dao.insertCategory(category.toEntity())
        }
    }

    override fun getCategories(): Flow<Either<DataError, DataResult<List<CategoryModel>>>> =
        networkBoundResource(
            localQuery = dao::observeCategories,
            apiFetch = {
                getRequestFlowDto {
                    categoryService.getCategories()
                }
            },
            saveFetchResult = { list ->
                transactionProvider.runAsTransaction {
                    for (category in list) {
                        addOrUpdateCategory(category)
                    }
                }

                shouldFetch = false
            },
            shouldFetch = shouldFetch
        ).mapDataResult {
            it.mapToDomain()
        }

    override suspend fun updateCategoriesSettings(vararg categories: CategoryModel) {
        transactionProvider.runAsTransaction {
            dao.updateCategories(*categories.map { it.toEntity() }.toTypedArray())
        }
    }
}
