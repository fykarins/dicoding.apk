package com.example.storyapp.data.remote

data class StoryResponse(
    val listStory: List<ListStoryItem>,
    val error: Boolean,
    val message: String
)

data class ListStoryItem(
    val photoUrl: String,
    val createdAt: String,
    val name: String,
    val description: String,
    val id: String,
    val lon: Double?,
    val lat: Double?
)

