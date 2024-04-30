package com.example.simbirsoftmobile.presentation.models.settingTest

import android.content.Context
import android.content.SharedPreferences
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.presentation.models.core.LocalError
import com.google.gson.GsonBuilder

object CategoryPrefsManager {

    private const val CATEGORY_SETTINGS_KEY = "CATEGORY_SETTINGS"
    private const val SHARED_PREF_NAME = "MY_SHARED_PREF"

    private val gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(
                CategorySettingSerializer.objectType,
                CategorySettingSerializer()
            )
            .registerTypeAdapter(
                CategorySettingDeserializer.objectType,
                CategorySettingDeserializer(),
            )
            .create()
    }

    fun getCategorySettings(context: Context): List<CategorySettingPrefs> {
        val pref: SharedPreferences =
            context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val categorySettingsJson = pref.getString(CATEGORY_SETTINGS_KEY, "").orEmpty()

        return gson.fromJson<List<CategorySettingPrefs>>(
            categorySettingsJson,
            CategorySettingDeserializer.objectType,
        ) ?: emptyList()
    }

    fun getSettingsEither(context: Context): Either<LocalError, List<CategorySettingPrefs>> {
        val pref: SharedPreferences =
            context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val categorySettingsJson = pref.getString(CATEGORY_SETTINGS_KEY, "")

        return if (categorySettingsJson == null) {
            Either.Left(LocalError(null))
        } else {
            Either.Right(
                gson.fromJson<List<CategorySettingPrefs>>(
                    categorySettingsJson,
                    CategorySettingDeserializer.objectType,
                ) ?: emptyList()
            )
        }
    }

    fun setCategorySettings(context: Context, settings: List<CategorySettingPrefs>) {
        val jsonList = gson.toJson(settings, CategorySettingSerializer.objectType)

        val pref: SharedPreferences =
            context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()

        editor.putString(CATEGORY_SETTINGS_KEY, jsonList)
        editor.apply()
    }
}
