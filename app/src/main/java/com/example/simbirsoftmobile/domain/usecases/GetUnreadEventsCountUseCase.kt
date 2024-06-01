package com.example.simbirsoftmobile.domain.usecases

import android.util.Log
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.example.simbirsoftmobile.domain.utils.extractResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class GetUnreadEventsCountUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val eventRepository: EventRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Either<DataError, Int>> =
        categoryRepository
            .getCategories()
            .extractResult()
            .flatMapLatest { either ->
                Log.d("GetUnreadEventsCountUseCase", "invoke: $either")
                when (either) {
                    is Either.Left -> flowOf(either)
                    is Either.Right -> {
                        eventRepository.observeUnreadEventsByCategories(
                            *either.value
                                .filter { it.isSelected }
                                .map { it.id }
                                .toTypedArray()
                        ).extractResult().onEach {
                            Log.d("GetUnreadEventsCountUseCase", "onEach: $it")
                        }
                    }
                }
            }
}