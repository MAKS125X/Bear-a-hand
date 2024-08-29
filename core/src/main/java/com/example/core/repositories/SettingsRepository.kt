package com.example.core.repositories

import com.example.result.DataError
import com.example.result.Either

interface SettingsRepository {
    suspend fun getShowNotificationSetting(): Either<DataError, Boolean>

    fun getShowNotificationStatus(): Boolean

    suspend fun setShowNotificationSetting(newStatus: Boolean): Either<DataError, Unit>

    suspend fun getProfileImageStringUri(): Either<DataError, String>

    suspend fun deleteProfileImage(): Either<DataError, Unit>

    suspend fun updateImageByStringUri(newImageUriString: String): Either<DataError, Unit>
}