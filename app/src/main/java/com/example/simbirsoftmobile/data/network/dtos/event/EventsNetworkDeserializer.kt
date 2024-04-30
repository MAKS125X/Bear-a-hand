package com.example.simbirsoftmobile.data.network.dtos.event

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class EventsNetworkDeserializer : JsonDeserializer<List<EventDto>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): List<EventDto> {
        val jsonObject =
            json?.asJsonArray ?: throw JsonParseException("Ошибка преобразования json: $json")

        val events = mutableListOf<EventDto>()

        for (eventJson in jsonObject) {
            val event = eventJson.asJsonObject
            val title = event["name"].asString
            val id = event["id"].asString
            val description = event["description"].asString
            val photo = event["photo"].asString
            val status = event["status"].asInt
            val organizerName = event["organisation"].asString
            val category = event["category"].asString
            val address = event["address"].asString
            val email = event["email"].asString
            val url = event["url"].asString
            val phone = event["phone"].asString
            val createAt = event["createAt"].asLong
            val start = event["startDate"].asLong
            val end = event["endDate"].asLong

            events.add(
                EventDto(
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
                    url,
                )
            )
        }

        return events
    }

    companion object {
        val typeToken: Type = TypeToken.getParameterized(List::class.java, EventDto::class.java).type
    }
}
