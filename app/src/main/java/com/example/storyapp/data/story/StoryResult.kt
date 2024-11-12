package com.example.storyapp.data.story

sealed class StoryResult<out R> private constructor() {
    data class Success<out T>(val data: T) : StoryResult<T>()
    data class Error(val error: String) : StoryResult<Nothing>()
    data object Loading : StoryResult<Nothing>()
}