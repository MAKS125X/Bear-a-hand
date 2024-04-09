package com.example.simbirsoftmobile.presentation.models.event

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import kotlinx.datetime.LocalDate
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: Int,
    val organizerName: String,
    val categoryList: List<Int>,
    val address: String,
    val phoneNumbers: List<String>,
    val email: String,
    val siteUrl: String,
    val dateStart: @RawValue LocalDate,
    val dateEnd: @RawValue LocalDate,
) : Parcelable {
    companion object : Parceler<Event> {
        override fun Event.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(id)
            parcel.writeString(title)
            parcel.writeString(description)
            parcel.writeInt(imageUrl)
            parcel.writeString(organizerName)
            parcel.writeList(categoryList)
            parcel.writeString(address)
            parcel.writeList(phoneNumbers)
            parcel.writeString(email)
            parcel.writeString(siteUrl)
            parcel.writeInt(dateStart.toEpochDays())
            parcel.writeInt(dateEnd.toEpochDays())
        }

        override fun create(parcel: Parcel): Event {
            return Event(
                parcel.readInt(),
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readInt(),
                parcel.readString()!!,
                mutableListOf<Int>().apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        parcel.readList(
                            this, Int::class.java.classLoader, Int::class.java
                        )
                    }
                },
                parcel.readString()!!,
                mutableListOf<String>().apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        parcel.readList(
                            this, String::class.java.classLoader, String::class.java
                        )
                    }
                },
                parcel.readString()!!,
                parcel.readString()!!,
                LocalDate.fromEpochDays(parcel.readInt()),
                LocalDate.fromEpochDays(parcel.readInt()),
            )
        }
    }
}
