package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.local.StoryDatabase
import com.example.storyapp.data.remote.ApiConfig
import com.example.storyapp.data.story.AuthRepository
import com.example.storyapp.data.story.StoryRepository

object Injection {
    @Suppress("UNUSED_PARAMETER")
    fun authRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService)
    }

    fun storyRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService, database)
    }

}