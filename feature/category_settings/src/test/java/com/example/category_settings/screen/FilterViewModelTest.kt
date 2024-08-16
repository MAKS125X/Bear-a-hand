package com.example.category_settings.screen

import com.example.category_settings.model.SettingUi
import com.example.category_settings.model.mapToUi
import com.example.category_settings.usecase.GetCategorySettingsUseCase
import com.example.category_settings.usecase.UpdateCategoriesSettingsUseCase
import com.example.core.models.category.CategoryModel
import com.example.dataerror.getDescription
import com.example.result.DataError
import com.example.result.Either
import com.example.test_rules.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
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
class FilterViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private lateinit var viewModel: FilterViewModel
    private lateinit var getCategorySettingsUseCase: GetCategorySettingsUseCase
    private lateinit var updateCategoriesSettingsUseCase: UpdateCategoriesSettingsUseCase

    @Before
    fun setUp() {
        getCategorySettingsUseCase = mock()
        updateCategoriesSettingsUseCase = mock()
        viewModel = FilterViewModel(getCategorySettingsUseCase, updateCategoriesSettingsUseCase)
    }

    @Test
    fun categoriesLoaded_LoadingState_HoldsStateWithLoading() = runTest {
        assertEquals(
            FilterState(
                isLoading = true,
                categories = emptyList(),
                error = null,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun categoriesLoaded_CategoriesListLoaded_HoldsStateWithCategories() = runTest {
        val categoryLongUiList: List<SettingUi> = List(3) {
            SettingUi("id$it", "name$it", "nameEn$it", "imageUrl$it", false)
        }

        viewModel.consumeEvent(FilterEvent.Internal.CategoriesLoaded(categoryLongUiList))

        advanceUntilIdle()

        assertEquals(
            FilterState(
                isLoading = false,
                categories = categoryLongUiList,
                error = null,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun categoriesLoaded_ErrorLoaded_HoldsStateWithError() = runTest {
        val error = DataError.Unexpected()

        viewModel.consumeEvent(FilterEvent.Internal.ErrorLoaded(error))

        advanceUntilIdle()

        assertEquals(
            FilterState(
                isLoading = false,
                categories = emptyList(),
                error = error.getDescription(),
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun loadCategories_NotEmptyCategoriesListLoaded_HoldsStateWithCategories() = runTest {
        val categoryModel = CategoryModel("id", "name", "nameEn", "imageUrl", false)
        val categoryLongUi = categoryModel.mapToUi()

        val categoryModelList: List<CategoryModel> = listOf(categoryModel)
        val categoryLongUiList: List<SettingUi> = listOf(categoryLongUi)

        whenever(getCategorySettingsUseCase.invoke())
            .thenReturn(
                flow {
                    emit(Either.Right(categoryModelList))
                }
            )

        viewModel.consumeEvent(FilterEvent.Internal.LoadCategories)

        advanceUntilIdle()

        assertEquals(
            FilterState(
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

        val getCategoriesUseCase: GetCategorySettingsUseCase = mock()
        whenever(getCategoriesUseCase.invoke())
            .thenReturn(
                flow {
                    emit(Either.Right(categoryModelList))
                }
            )

        viewModel.consumeEvent(FilterEvent.Internal.LoadCategories)

        advanceUntilIdle()

        assertEquals(
            FilterState(
                isLoading = false,
                categories = emptyList(),
                error = error.getDescription(),
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun loadCategories_ErrorLoaded_HoldsStateWithError() = runTest {
        val error = DataError.Unexpected()

        whenever(getCategorySettingsUseCase.invoke())
            .thenReturn(
                flow {
                    emit(Either.Left(error))
                }
            )

        viewModel.consumeEvent(FilterEvent.Internal.LoadCategories)

        advanceUntilIdle()

        assertEquals(
            FilterState(
                isLoading = false,
                categories = emptyList(),
                error = error.getDescription(),
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun changeCategories_UpdateCategory_HoldsStateUpdatedCategories() = runTest {
        val categories: List<SettingUi> = List(5) {
            SettingUi("$it", "name$it", "nameEn$it", "imageUrl$it", false)
        }

        viewModel.consumeEvent(FilterEvent.Internal.CategoriesLoaded(categories))

        advanceUntilIdle()

        val changedId = categories.first().id

        val changedSetting = categories.first { it.id == changedId }
        changedSetting.isSelected = !changedSetting.isSelected

        viewModel.consumeEvent(FilterEvent.Ui.UpdateSelectedById(changedId))

        advanceUntilIdle()

        assertEquals(
            categories,
            viewModel.state.value.categories,
        )
    }

    @Test
    fun updateChanges_SuccessUpdating_ProcessSideSuccessSideEffect() = runTest {
        val deferredEffect = async {
            viewModel.effects.first()
        }

        viewModel.consumeEvent(FilterEvent.Ui.AcceptChanges)

        whenever(updateCategoriesSettingsUseCase.invoke())
            .thenReturn(
                flow {
                    emit(Either.Right(Unit))
                }
            )

        advanceUntilIdle()

        assertEquals(
            FilterSideEffect.SuccessChangesUpdate,
            deferredEffect.await(),
        )
    }

    @Test
    fun updateChanges_ErrorUpdating_ProcessSideSuccessSideEffect() = runTest {
        val deferredEffect = async {
            viewModel.effects.first()
        }

        val updatingError = DataError.Unexpected()

        whenever(updateCategoriesSettingsUseCase.invoke())
            .thenReturn(
                flow {
                    emit(Either.Left(updatingError))
                }
            )

        viewModel.consumeEvent(FilterEvent.Ui.AcceptChanges)

        advanceUntilIdle()

        assertEquals(
            FilterSideEffect.ErrorChangesUpdate(updatingError),
            deferredEffect.await(),
        )
    }
}
