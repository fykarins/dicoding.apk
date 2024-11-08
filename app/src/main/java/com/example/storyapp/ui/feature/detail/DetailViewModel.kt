package com.example.storyapp.ui.feature.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.DetailStoryResponse
import com.example.storyapp.data.story.StoryRepository
import com.example.storyapp.data.story.StoryResult
import kotlinx.coroutines.launch

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _story = MutableLiveData<DetailStoryResponse>()
    val story: LiveData<DetailStoryResponse> = _story

    fun getStory(token: String, id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = storyRepository.getStory(token, id)
            if (result is StoryResult.Success) {
                _isLoading.value = false
                _story.value = result.data
            } else {
                _isLoading.value = false
                StoryResult.Error("Failed to fetch story")
            }
        }
    }

}