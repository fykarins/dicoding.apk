package com.example.storyapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(appKey: List<AppEntity>)

    @Query("SELECT * FROM app_keys WHERE id = :id")
    suspend fun getAppEntityId(id: String): AppEntity?

    @Query("DELETE FROM app_keys")
    suspend fun deleteAppKeys()
}