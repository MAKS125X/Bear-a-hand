package com.example.event_details.usecase

import com.example.core.repositories.SettingsRepository
import javax.inject.Inject

class GetNotificationStatusUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke() = settingsRepository.getShowNotificationStatus()
}