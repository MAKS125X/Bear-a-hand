package com.example.news.useCase

import com.example.core.models.event.EventModel
import com.example.core.repositories.CategoryRepository
import com.example.core.repositories.EventRepository
import com.example.result.DataError
import com.example.result.Either
import com.example.result.extractResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GetEventsBySettingsUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val eventRepository: EventRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Either<DataError, List<EventModel>>> =
        categoryRepository
            .getCategories()
            .extractResult()
            .flatMapLatest { categories ->
                when (categories) {
                    is Either.Left -> {
                        eventRepository.getAllEvents()
                            .extractResult()
                    }

                    is Either.Right -> {
                        eventRepository
                            .getEventsByCategory(
                                *categories.value
                                    .filter { it.isSelected }
                                    .map { it.id }
                                    .toTypedArray()
                            )
                            .extractResult()
                    }
                }
            }
}
