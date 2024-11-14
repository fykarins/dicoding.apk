package com.example.storyapp.ui.feature.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.local.ListStoryItem
import com.example.storyapp.data.story.StoryRepository

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun stories(token: String): LiveData<PagingData<ListStoryItem>> {
        _isLoading.value = true
        return storyRepository.getPaginatedStories(token)
            .cachedIn(viewModelScope)
            .also { _isLoading.value = false }
    }
}