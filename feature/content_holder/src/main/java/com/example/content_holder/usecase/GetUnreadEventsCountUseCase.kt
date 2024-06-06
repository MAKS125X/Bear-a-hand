package com.example.content_holder.usecase

import com.example.core.repositories.CategoryRepository
import com.example.core.repositories.EventRepository
import com.example.result.DataError
import com.example.result.Either
import com.example.result.extractResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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
                when (either) {
                    is Either.Left -> flowOf(either)
                    is Either.Right -> {
                        eventRepository.observeUnreadEventsByCategories(
                            *either.value
                                .filter { it.isSelected }
                                .map { it.id }
                                .toTypedArray()
                        ).extractResult()
                    }
                }
            }
}
