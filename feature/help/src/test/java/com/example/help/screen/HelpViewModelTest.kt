package com.example.help.screen

import com.example.core.models.category.CategoryModel
import com.example.dataerror.getDescription
import com.example.help.model.CategoryLongUi
import com.example.help.model.mapToUi
import com.example.help.usecase.GetCategoriesUseCase
import com.example.result.DataError
import com.example.result.Either
import com.example.test_rules.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HelpViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private lateinit var viewModel: HelpViewModel
    private lateinit var getCategoriesUseCase: GetCategoriesUseCase

    @Before
    fun setUp() {
        getCategoriesUseCase = mock()
        viewModel = HelpViewModel(getCategoriesUseCase)
    }

    @Test
    fun creation_LoadingState_HoldsStateWithLoading() = runTest {
        assertEquals(
            HelpState(
                isLoading = true,
                categories = emptyList(),
                error = null,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun consumeEvent_CategoriesListLoaded_HoldsStateWithCategories() = runTest {
        val categoryLongUi = CategoryLongUi("id", "name", "nameEn", "imageUrl", false)

        val categoryLongUiList: List<CategoryLongUi> = listOf(categoryLongUi)

        viewModel.consumeEvent(HelpEvent.Internal.CategoriesLoaded(categoryLongUiList))

        advanceUntilIdle()

        assertEquals(
            HelpState(
                isLoading = false,
                categories = categoryLongUiList,
                error = null,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun consumeEvent_ErrorLoaded_HoldsStateWithError() = runTest {
        val error = DataError.Unexpected()

        viewModel.consumeEvent(HelpEvent.Internal.ErrorLoaded(error))

        advanceUntilIdle()

        assertEquals(
            HelpState(
                isLoading = false,
                categories = emptyList(),
                error = error.getDescription(),
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun loadCategories_NotEmptyCategoriesListLoaded_HoldsStateWithCategories() = runTest {
        val categoryModel = CategoryModel("", "", "", "", false)
        val categoryLongUi = categoryModel.mapToUi()

        val categoryModelList: List<CategoryModel> = listOf(categoryModel)
        val categoryLongUiList: List<CategoryLongUi> = listOf(categoryLongUi)

        whenever(getCategoriesUseCase.invoke())
            .thenReturn(
                flow {
                    emit(Either.Right(categoryModelList))
                }
            )

        viewModel.consumeEvent(HelpEvent.Internal.LoadCategories)

        advanceUntilIdle()

        assertEquals(
            HelpState(
                isLoading = false,
                categories = categoryLongUiList,
                error = null,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun loadCategories_EmptyListCategoriesLoaded_HoldsStateWithError() = runTest {
        val error = DataError.Unexpected()
        val categoryModelList: List<CategoryModel> = emptyList()

        val getCategoriesUseCase: GetCategoriesUseCase = mock()
        whenever(getCategoriesUseCase.invoke())
            .thenReturn(
                flow {
                    emit(Either.Right(categoryModelList))
                }
            )

        viewModel.consumeEvent(HelpEvent.Internal.LoadCategories)

        advanceUntilIdle()

        assertEquals(
            HelpState(
                isLoading = false,
                categories = emptyList(),
                error = error.getDescription(),
            ),
            viewModel.state.value,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loadCategories_ErrorLoaded_HoldsStateWithError() = runTest {
        val error = DataError.Unexpected()

        val getCategoriesUseCase: GetCategoriesUseCase = mock()

        whenever(getCategoriesUseCase.invoke())
            .thenReturn(
                flow {
                    emit(Either.Left(error))
                }
            )

        viewModel.consumeEvent(HelpEvent.Internal.LoadCategories)

        advanceUntilIdle()

        assertEquals(
            HelpState(
                isLoading = false,
                categories = emptyList(),
                error = error.getDescription(),
            ),
            viewModel.state.value,
        )
    }
}
