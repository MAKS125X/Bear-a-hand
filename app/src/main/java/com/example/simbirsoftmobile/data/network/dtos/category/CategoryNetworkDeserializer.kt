package com.example.simbirsoftmobile.data.network.dtos.category

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class CategoryNetworkDeserializer : JsonDeserializer<List<CategoryDto>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): List<CategoryDto> {
        val categories = mutableListOf<CategoryDto>()

        val jsonObject =
            json?.asJsonObject ?: throw JsonParseException("Ошибка преобразования json: $json")

        for (entry in jsonObject.entrySet()) {
            val value = entry.value.asJsonObject

            val id = value["id"].asString
            val name: String = value["name"].asString
            val nameEn: String = value["name_en"].asString
            val imageUrl: String = value["image"].asString

            categories.add(CategoryDto(id, name, nameEn, imageUrl))
        }
        return categories
    }

    companion object {
        val typeToken: Type = TypeToken.getParameterized(List::class.java, CategoryDto::class.java).type
    }
}
