package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.di.SimbirSoftApp
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.models.event.OrganizationModel
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.example.simbirsoftmobile.domain.utils.extractResult
import kotlinx.coroutines.flow.Flow

class SearchOrganizationsUseCase(
    private val repository: EventRepository = SimbirSoftApp.INSTANCE.appContainer.eventRepository,
) {
    operator fun invoke(query: String): Flow<Either<DataError, List<OrganizationModel>>> =
        repository.searchOrganizations("%$query%").extractResult()
}
