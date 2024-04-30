package com.example.simbirsoftmobile.presentation.models.settingTest

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class CategorySettingSerializer : JsonSerializer<List<CategorySettingPrefs>> {
    override fun serialize(
        src: List<CategorySettingPrefs>?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?,
    ): JsonElement {
        if (src == null) {
            throw NullPointerException()
        } else {
            val result = JsonObject()
            val array = JsonArray()
            for (setting in src) {
                val categoryJson = JsonObject()
                categoryJson.addProperty("id", setting.id)
                categoryJson.addProperty("selected", setting.isSelected)
                array.add(categoryJson)
            }
            result.add("settings", array)

            return result
        }
    }

    companion object {
        val objectType: Type = object : TypeToken<List<CategorySettingPrefs>>() {}.type
    }
}
