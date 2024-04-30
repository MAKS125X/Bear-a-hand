package com.example.simbirsoftmobile.data.network.dtos.event

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class EventNetworkDeserializer : JsonDeserializer<EventDto> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): EventDto {
        val jsonObject =
            json?.asJsonObject ?: throw JsonParseException("Ошибка преобразования json: $json")

        val title = jsonObject["name"].asString
        val id = jsonObject["id"].asString
        val description = jsonObject["description"].asString
        val photo = jsonObject["photo"].asString
        val status = jsonObject["status"].asInt
        val organizerName = jsonObject["organisation"].asString
        val category = jsonObject["category"].asString
        val address = jsonObject["address"].asString
        val email = jsonObject["email"].asString
        val url = jsonObject["url"].asString
        val phone = jsonObject["phone"].asString
        val createAt = jsonObject["createAt"].asLong
        val start = jsonObject["startDate"].asLong
        val end = jsonObject["endDate"].asLong

        return EventDto(
            id,
            title,
            start,
            end,
            description,
            status,
            photo,
            category,
            createAt,
            phone,
            address,
            email,
            organizerName,
            url
        )
    }

    companion object {
        val objectType: Type = object : TypeToken<EventDto>() {}.type
    }
}
