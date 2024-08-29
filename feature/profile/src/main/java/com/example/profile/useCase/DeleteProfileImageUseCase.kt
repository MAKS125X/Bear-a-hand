package com.example.profile.useCase

import com.example.core.repositories.SettingsRepository
import javax.inject.Inject

open class DeleteProfileImageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke() = settingsRepository.deleteProfileImage()
}