package com.example.profile.useCase

import com.example.core.repositories.SettingsRepository
import com.example.result.DataError
import com.example.result.Either
import javax.inject.Inject

open class GetProfileImageUriUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    open suspend operator fun invoke(): Either<DataError, String> =
        settingsRepository.getProfileImageStringUri()
}