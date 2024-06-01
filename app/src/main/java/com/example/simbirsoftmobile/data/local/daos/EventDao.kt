package com.example.simbirsoftmobile.data.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.simbirsoftmobile.data.local.entities.EventEntity
import com.example.simbirsoftmobile.data.local.entities.EventPartialEntity
import com.example.simbirsoftmobile.data.local.entities.toFullEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Update(entity = EventEntity::class)
    suspend fun updateEvent(event: EventPartialEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEvent(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEvents(event: List<EventEntity>)

    suspend fun insertOrUpdateEvent(event: EventPartialEntity) {
        val existingEvent = getEventById(event.id)
        if (existingEvent != null) {
            updateEvent(event)
        } else {
            addEvent(event.toFullEntity(false))
        }
    }

    suspend fun insertOrUpdateEvent(event: List<EventPartialEntity>) {
        event.forEach {
            insertOrUpdateEvent(it)
        }
    }

    @Query("SELECT * FROM events WHERE events.name LIKE :nameSubstring")
    fun searchEventsByName(nameSubstring: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE events.organization LIKE :organizationSubstring")
    fun searchEventsByOrganization(organizationSubstring: String): Flow<List<EventEntity>>

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("SELECT * FROM events")
    fun getEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE events.category_id IN (:categoryIds)")
    fun getEvents(categoryIds: List<String>): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id == :id")
    suspend fun getEventById(id: String): EventEntity?

    @Query("SELECT * FROM events WHERE id == :id")
    fun observeEventById(id: String): Flow<EventEntity>

    @Query("SELECT COUNT(*) FROM events WHERE isRead == 0 AND events.category_id IN (:categoryIds)")
    fun observeUnreadEventsCountByCategory(categoryIds: List<String>): Flow<Int>

    @Query("UPDATE events SET isRead = 1 WHERE id == :id")
    fun changeIsReadStateById(id: String)

    fun observeEventWithUpdatingState(eventId: String): Flow<EventEntity> {
        changeIsReadStateById(eventId)
        return observeEventById(eventId)
    }
}
