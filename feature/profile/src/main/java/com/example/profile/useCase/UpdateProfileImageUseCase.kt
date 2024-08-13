package com.example.profile.useCase

import com.example.core.repositories.SettingsRepository
import javax.inject.Inject

class UpdateProfileImageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(newImageUriString: String) = settingsRepository.updateImageByStringUri(newImageUriString)
}