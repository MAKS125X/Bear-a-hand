package com.example.simbirsoftmobile.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.simbirsoftmobile.presentation.models.category.Category
import com.example.simbirsoftmobile.presentation.models.category.CategoryDeserializer
import com.example.simbirsoftmobile.presentation.models.category.CategorySetting
import com.example.simbirsoftmobile.presentation.models.category.CategorySettingDeserializer
import com.example.simbirsoftmobile.presentation.models.category.CategorySettingSerializer
import com.example.simbirsoftmobile.presentation.models.category.toSettingsModel
import com.google.gson.GsonBuilder

object CategoryRepository {
    private const val CATEGORY_SETTINGS_KEY = "CATEGORY_SETTINGS"
    private const val SHARED_PREF_NAME = "MY_SHARED_PREF"

    private val gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(
                CategoryDeserializer.objectType,
                CategoryDeserializer(),
            )
            .registerTypeAdapter(CategorySettingSerializer.objectType, CategorySettingSerializer())
            .registerTypeAdapter(
                CategorySettingDeserializer.objectType,
                CategorySettingDeserializer(),
            )
            .create()
    }

    fun getCategories(context: Context): List<Category> = try {
        Thread.sleep(3_000)
        val json = context.assets
            .open("categories.json")
            .bufferedReader()
            .use {
                it.readText()
            }

        gson.fromJson(json, CategoryDeserializer.objectType)
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()

        emptyList()
    }

    fun saveCategorySettings(
        context: Context,
        settings: List<CategorySetting>,
    ) {
        val jsonList =
            gson.toJson(settings, CategorySettingSerializer.objectType)
        val pref: SharedPreferences =
            context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()

        editor.putString(CATEGORY_SETTINGS_KEY, jsonList)
        editor.apply()
    }

    fun getCategorySettings(context: Context): List<CategorySetting> {
        val pref: SharedPreferences =
            context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val jsonList = pref.getString(CATEGORY_SETTINGS_KEY, "")

        if (jsonList.isNullOrEmpty()) {
            return getCategories(context).map { it.toSettingsModel() }
        }

        return gson.fromJson(
            jsonList,
            CategorySettingDeserializer.objectType,
        )
    }

    fun getSelectedCategoriesId(context: Context): List<Int> {
        val settings = getCategorySettings(context)
        Thread.sleep(2000)
        return settings.filter { it.isSelected }.map { it.category.id }
    }
}
