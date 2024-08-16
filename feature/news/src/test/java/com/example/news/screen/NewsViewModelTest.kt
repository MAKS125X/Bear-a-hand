package com.example.news.screen

import com.example.core.models.event.EventModel
import com.example.dataerror.getDescription
import com.example.news.models.EventShortUi
import com.example.news.models.mapToShorUi
import com.example.news.useCase.GetEventsBySettingsUseCase
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
class NewsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private lateinit var viewModel: NewsViewModel
    private lateinit var getNewsUseCase: GetEventsBySettingsUseCase

    @Before
    fun setUp() {
        getNewsUseCase = mock()
        viewModel = NewsViewModel(getNewsUseCase)
    }

    @Test
    fun creation_LoadingState_HoldsStateWithLoading() = runTest {
        assertEquals(
            NewsState(
                isLoading = true,
                news = emptyList(),
                error = null,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun consumeEvent_NewsLoaded_HoldsStateWithNews() = runTest {
        val newShortUi = EventShortUi(
            "id",
            "name",
            0L,
            2L,
            "description",
            3,
            "photo",
            "categoryId",
            1L,
        )
        val newShortUiList: List<EventShortUi> = listOf(newShortUi)

        viewModel.consumeEvent(NewsEvent.Internal.NewsLoaded(newShortUiList))

        advanceUntilIdle()

        assertEquals(
            NewsState(
                isLoading = false,
                news = newShortUiList,
                error = null,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun consumeEvent_ErrorLoaded_HoldsStateWithError() = runTest {
        val error = DataError.Unexpected()

        viewModel.consumeEvent(NewsEvent.Internal.ErrorLoaded(error))

        advanceUntilIdle()

        assertEquals(
            NewsState(
                isLoading = false,
                news = emptyList(),
                error = error.getDescription(),
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun loadNews_NotEmptyNewsListLoaded_HoldsStateWithNews() = runTest {
        val newModel = EventModel(
            "id",
            "name",
            0L,
            2L,
            "description",
            3,
            "photo",
            "categoryId",
            1L,
            "phone",
            "address",
            "email",
            "organization",
            "url",
            false
        )
        val newShortUi = newModel.mapToShorUi()

        val newsModelList: List<EventModel> = listOf(newModel)
        val newShortUiList: List<EventShortUi> = listOf(newShortUi)

        whenever(getNewsUseCase.invoke())
            .thenReturn(
                flow {
                    emit(Either.Right(newsModelList))
                }
            )

        viewModel.consumeEvent(NewsEvent.Internal.LoadNews)

        advanceUntilIdle()

        assertEquals(
            NewsState(
                isLoading = false,
                news = newShortUiList,
                error = null,
            ),
            viewModel.state.value,
        )
    }

    @Test
    fun loadNews_EmptyListNewsListLoaded_HoldsStateWithEmptyList() = runTest {
        val newsModelList: List<EventModel> = emptyList()

        whenever(getNewsUseCase.invoke())
            .thenReturn(
                flow {
                    emit(Either.Right(newsModelList))
                }
            )

        viewModel.consumeEvent(NewsEvent.Internal.LoadNews)

        advanceUntilIdle()

        assertEquals(
            NewsState(
                isLoading = false,
                news = emptyList(),
                error = null,
            ),
            viewModel.state.value,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loadNews_ErrorLoaded_HoldsStateWithError() = runTest {
        val error = DataError.Unexpected()

        whenever(getNewsUseCase.invoke())
            .thenReturn(
                flow {
                    emit(Either.Left(error))
                }
            )

        viewModel.consumeEvent(NewsEvent.Internal.ErrorLoaded(error))

        advanceUntilIdle()

        assertEquals(
            NewsState(
                isLoading = false,
                news = emptyList(),
                error = error.getDescription(),
            ),
            viewModel.state.value,
        )
    }
}