package com.example.simbirsoftmobile.data.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.simbirsoftmobile.data.local.entities.CategorySettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: CategorySettingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(vararg categories: CategorySettingEntity)

    @Delete
    suspend fun deleteCategory(category: CategorySettingEntity)

    @Update
    fun updateCategories(vararg categories: CategorySettingEntity): Int

    @Query("SELECT * FROM category")
    fun observeCategories(): Flow<List<CategorySettingEntity>>

    @Query("SELECT * FROM category WHERE id = :categoryId")
    fun getCategoryById(categoryId: String): CategorySettingEntity?
}
