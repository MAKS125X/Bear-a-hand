package com.example.profile.useCase

import com.example.core.repositories.SettingsRepository
import javax.inject.Inject

class GetShowNotificationSettingUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke() = settingsRepository.getShowNotificationSetting()
}