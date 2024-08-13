package com.example.android_settings

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.core.repositories.SettingsRepository
import com.example.result.DataError
import com.example.result.Either
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class AndroidSettingsRepository @Inject constructor(private val context: Context) :
    SettingsRepository {
    private val pref: SharedPreferences =
        context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    private val profilePhotoFile: File
        get() {
            val tempImagesDir =
                File(
                    context.filesDir,
                    TEMP_IMAGES_DIR_NAME,
                )
            tempImagesDir.mkdir()

            return File(
                tempImagesDir,
                TEMP_IMAGE_NAME,
            )
        }

    private val profilePhotoUri: Uri
        get() = FileProvider.getUriForFile(
            context,
            AUTHORITIES,
            profilePhotoFile,
        )


    private fun copyImageToProfileLocation(imageUri: Uri): Boolean {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val outputStream = FileOutputStream(profilePhotoFile)

        if (inputStream == null) {
            return false
        }

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return true
    }

    override suspend fun updateImageByStringUri(newImageUriString: String): Either<DataError, Unit> {
        val result = copyImageToProfileLocation(Uri.parse(newImageUriString))

        return if (result) Either.Right(Unit)
        else Either.Left(DataError.Unexpected())
    }

    override suspend fun getProfileImageStringUri(): Either<DataError, String> {
        return Either.Right(profilePhotoUri.toString())
    }

    override suspend fun deleteProfileImage(): Either<DataError, Unit> {
        if (profilePhotoFile.exists()) {
            val result = profilePhotoFile.delete()

            return if (result) Either.Right(Unit)
            else Either.Left(DataError.Unexpected())
        }

        return Either.Right(Unit)
    }

    override suspend fun getShowNotificationSetting(): Either<DataError, Boolean> {
        val value = pref.getBoolean(SHOW_NOTIFICATION_SETTING_KEY, false)

        return Either.Right(value)
    }

    override fun getShowNotificationStatus(): Boolean {
        return pref.getBoolean(SHOW_NOTIFICATION_SETTING_KEY, false)
    }

    override suspend fun setShowNotificationSetting(newStatus: Boolean): Either<DataError, Unit> {
        with(pref.edit()) {
            putBoolean(SHOW_NOTIFICATION_SETTING_KEY, newStatus)
            apply()
        }

        return Either.Right(Unit)
    }

    companion object {
        const val SHARED_PREF_NAME = "MY_SHARED_PREF"
        const val SHOW_NOTIFICATION_SETTING_KEY = "SHOW_NOTIFICATION_SETTING"
        const val TEMP_IMAGES_DIR_NAME = "temp_images"
        const val TEMP_IMAGE_NAME = "temp_image"
        const val AUTHORITIES = "com.example.fileprovider"
    }
}