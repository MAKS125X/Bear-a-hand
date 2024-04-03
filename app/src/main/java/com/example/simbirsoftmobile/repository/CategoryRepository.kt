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
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

object CategoryRepository {
    private const val CATEGORY_SETTINGS_KEY = "CATEGORY_SETTINGS"
    private const val SHARED_PREF_NAME = "MY_SHARED_PREF"

    const val TAG = "CategoryRepository"

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

    fun getCategories(context: Context): Observable<List<Category>> {
        return Observable
            .just(context.assets
                .open("categories.json")
                .bufferedReader()
                .use { it.readText() })
            .subscribeOn(Schedulers.io())
            .delay(1000, TimeUnit.MILLISECONDS)
            .map {
                gson.fromJson<List<Category>>(it, CategoryDeserializer.objectType)
            }
    }

    fun saveCategorySettings(
        context: Context,
        settings: List<CategorySetting>,
    ) {
        val jsonList = gson.toJson(settings, CategorySettingSerializer.objectType)
        val pref: SharedPreferences =
            context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()

        editor.putString(CATEGORY_SETTINGS_KEY, jsonList)
        editor.apply()
    }

    fun getCategorySettings(context: Context): Observable<List<CategorySetting>> {
        val pref: SharedPreferences =
            context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return Observable
            .just(pref.getString(CATEGORY_SETTINGS_KEY, "") ?: "")
            .delay(2000, TimeUnit.MILLISECONDS)
            .map {
                gson.fromJson<List<CategorySetting>>(
                    it,
                    CategorySettingDeserializer.objectType,
                ) ?: mutableListOf()
            }
            .flatMap { settingsList ->
                if (settingsList.isEmpty()) {
                    getCategories(context)
                        .map { categoriesList ->
                            categoriesList.map { it.toSettingsModel() }
                        }
                } else {
                    Observable.just(settingsList)
                }
            }
    }

    fun getSelectedCategoriesId(context: Context): Observable<List<Int>> {
        return getCategorySettings(context)
            .map {
                it.filter { category -> category.isSelected }
                    .map { category -> category.category.id }
            }
    }
}
