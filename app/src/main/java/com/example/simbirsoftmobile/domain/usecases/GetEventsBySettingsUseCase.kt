package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.models.event.EventModel
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.example.simbirsoftmobile.domain.utils.UnreadNewsController
import com.example.simbirsoftmobile.domain.utils.extractResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
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
                        eventRepository.getAllEvents().extractResult()
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
                            .onEach {
                                if (it is Either.Right) {
                                    UnreadNewsController.emitUnreadValue(it.value)
                                }
                            }
                    }
                }
            }
}
