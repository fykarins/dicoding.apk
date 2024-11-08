package com.example.storyapp.ui.feature.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.LoginResponse
import com.example.storyapp.data.story.AuthRepository
import com.example.storyapp.data.story.StoryResult
import com.example.storyapp.data.user.UserPreferences
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<StoryResult<LoginResponse>>()
    val loginResult: LiveData<StoryResult<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            _loginResult.value = result
            if (result is StoryResult.Success) {
                result.data.loginResult?.token?.let { token ->
                    userPreferences.saveToken(token)
                }
            }
            _isLoading.value = false
        }
    }
}