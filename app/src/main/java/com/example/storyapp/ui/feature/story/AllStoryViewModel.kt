package com.example.storyapp.ui.feature.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.story.StoryResult
import com.example.storyapp.data.remote.ListStoryItem
import com.example.storyapp.data.story.StoryRepository
import kotlinx.coroutines.launch

class AllStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<StoryResult<List<ListStoryItem>>>()
    val stories: LiveData<StoryResult<List<ListStoryItem>>> = _stories

    fun getAllStories(token: String, page: Int? = null, size: Int? = null, location: Int = 0) {
        viewModelScope.launch {
            _stories.value =
                storyRepository.getAllStories(token, page, size, location).let { storyResult ->
                    if (storyResult is StoryResult.Success) {
                        StoryResult.Success(storyResult.data.listStory)
                    } else {
                        StoryResult.Error("Failed to fetch stories")
                    }
                }
        }
    }
}