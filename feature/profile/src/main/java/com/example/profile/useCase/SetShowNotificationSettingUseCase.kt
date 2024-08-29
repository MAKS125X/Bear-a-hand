package com.example.profile.useCase

import com.example.core.repositories.SettingsRepository
import javax.inject.Inject

open class SetShowNotificationSettingUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    open suspend operator fun invoke(newStatus: Boolean) =
        settingsRepository.setShowNotificationSetting(newStatus)
}