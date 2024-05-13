package com.example.simbirsoftmobile.data.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.simbirsoftmobile.data.local.entities.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEvents(event: List<EventEntity>)

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
    fun getEventById(id: String): Flow<EventEntity>
}
