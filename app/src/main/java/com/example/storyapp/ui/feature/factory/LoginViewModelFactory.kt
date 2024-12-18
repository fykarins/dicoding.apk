package com.example.storyapp.ui.feature.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.user.UserPreferences
import com.example.storyapp.data.story.AuthRepository
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.feature.login.LoginViewModel

class LoginViewModelFactory(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authRepository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: LoginViewModelFactory? = null

        fun getInstance(context: Context): LoginViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: LoginViewModelFactory(
                    Injection.authRepository(context),
                    UserPreferences(context)
                )
            }.also { instance = it }
    }
}