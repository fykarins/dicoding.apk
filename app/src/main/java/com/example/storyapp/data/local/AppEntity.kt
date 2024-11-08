package com.example.storyapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_keys")
data class AppEntity(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?
)