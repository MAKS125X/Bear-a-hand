package com.example.simbirsoftmobile.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.simbirsoftmobile.data.utils.DataMapper
import com.example.simbirsoftmobile.domain.models.event.EventModel
import com.example.simbirsoftmobile.domain.models.event.OrganizationModel

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = CategorySettingEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
        ),
    ],
)
data class EventEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "start_date") val startDate: Long,
    @ColumnInfo(name = "end_date") val endDate: Long,
    val description: String,
    val status: Int,
    val photo: String,
    @ColumnInfo(name = "category_id") val category: String,
    val createAt: Long,
    val phone: String,
    val email: String,
    val address: String,
    val organization: String,
    val url: String,
) : DataMapper<EventModel> {
    override fun mapToDomain() = EventModel(
        id,
        name,
        startDate,
        endDate,
        description,
        status,
        photo,
        category,
        createAt,
        phone,
        address,
        email,
        organization,
        url,
    )
}

fun EventEntity.toOrganization(): OrganizationModel = OrganizationModel(organization)
