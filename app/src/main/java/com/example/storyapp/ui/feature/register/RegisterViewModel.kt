package com.example.storyapp.ui.feature.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.RegisterResponse
import com.example.storyapp.data.story.AuthRepository
import com.example.storyapp.data.story.StoryResult
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _registerResult = MutableLiveData<StoryResult<RegisterResponse>>()
    val registerResult: LiveData<StoryResult<RegisterResponse>> = _registerResult

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true

        viewModelScope.launch {
            val storyResult = authRepository.register(name, email, password)
            _registerResult.value = storyResult
            _isLoading.value = false
        }
    }
}