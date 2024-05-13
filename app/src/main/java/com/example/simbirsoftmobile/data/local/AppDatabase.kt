package com.example.simbirsoftmobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.simbirsoftmobile.data.local.daos.CategoryDao
import com.example.simbirsoftmobile.data.local.daos.EventDao
import com.example.simbirsoftmobile.data.local.entities.CategorySettingEntity
import com.example.simbirsoftmobile.data.local.entities.EventEntity

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
