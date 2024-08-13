package com.example.profile.useCase

import com.example.core.repositories.SettingsRepository
import javax.inject.Inject

class SetShowNotificationSettingUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(newStatus: Boolean) =
        settingsRepository.setShowNotificationSetting(newStatus)
}