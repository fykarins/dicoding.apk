package com.example.storyapp.data.story

import com.example.storyapp.data.remote.LoginResponse
import com.example.storyapp.data.remote.RegisterResponse
import com.example.storyapp.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AuthRepository private constructor(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): StoryResult<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(email, password)

                if (response.error == false) {
                    StoryResult.Success(response)
                } else {
                    StoryResult.Error(response.message ?: "Unknown error occurred")
                }
            } catch (e: IOException) {
                StoryResult.Error("Network error: ${e.message}")
            } catch (e: HttpException) {
                StoryResult.Error("HTTP error: ${e.message}")
            } catch (e: Exception) {
                StoryResult.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    suspend fun register(name: String, email: String, password: String): StoryResult<RegisterResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(name, email, password)

                if (!response.error) {
                    StoryResult.Success(response)
                } else {
                    StoryResult.Error(response.message)
                }
            } catch (e: IOException) {
                StoryResult.Error("Network error: ${e.message}")
            } catch (e: HttpException) {
                StoryResult.Error("HTTP error: ${e.message}")
            } catch (e: Exception) {
                StoryResult.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService)
            }.also { instance = it }
    }
}