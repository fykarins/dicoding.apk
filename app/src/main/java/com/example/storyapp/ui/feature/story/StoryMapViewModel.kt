package com.example.storyapp.ui.feature.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.ListStoryItem
import com.example.storyapp.data.story.StoryRepository
import com.example.storyapp.data.story.StoryResult
import kotlinx.coroutines.launch

class StoryMapViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _stories = MutableLiveData<StoryResult<List<ListStoryItem>>>()
    val stories: LiveData<StoryResult<List<ListStoryItem>>> = _stories

    fun getAllStories(
        token: String,
        location: Int = 1
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            _stories.value =
                storyRepository.getAllStories(token, location).let { storyResult ->
                    if (storyResult is StoryResult.Success) {
                        _isLoading.value = false
                        StoryResult.Success(storyResult.data.listStory)
                    } else {
                        _isLoading.value = false
                        StoryResult.Error("Failed to fetch stories")
                    }
                }
        }
    }
}