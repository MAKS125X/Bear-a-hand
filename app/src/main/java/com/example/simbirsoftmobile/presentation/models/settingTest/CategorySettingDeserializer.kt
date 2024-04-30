package com.example.simbirsoftmobile.presentation.models.settingTest

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class CategorySettingDeserializer : JsonDeserializer<List<CategorySettingPrefs>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): List<CategorySettingPrefs> {
        val jsonSettingsArray =
            json?.asJsonObject?.get("settings")?.asJsonArray
                ?: throw IllegalArgumentException("Incorrect data format")

        val result =
            mutableListOf<CategorySettingPrefs>()

        for (jsonSetting in jsonSettingsArray) {
            val setting = jsonSetting.asJsonObject
            val id = setting["id"].asString
            val isSelected = setting["selected"].asBoolean

            result.add(CategorySettingPrefs(id, isSelected))
        }

        return result
    }

    companion object {
        val objectType: Type = object : TypeToken<List<CategorySettingPrefs>>() {}.type
    }
}
