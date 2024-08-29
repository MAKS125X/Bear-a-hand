package com.example.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.local.daos.CategoryDao
import com.example.local.daos.EventDao
import com.example.local.entities.CategorySettingEntity
import com.example.local.entities.EventEntity

@Database(
    version = 1,
    entities = [
        CategorySettingEntity::class,
        EventEntity::class,
    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categories(): CategoryDao

    abstract fun events(): EventDao
}
