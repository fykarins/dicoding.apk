package com.example.storyapp.ui.feature.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.story.StoryRepository
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.feature.newstory.NewStoryViewModel

class NewStoryViewModelFactory(
    private val storyRepository: StoryRepository,

    ) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewStoryViewModel::class.java)) {
            return NewStoryViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: NewStoryViewModelFactory? = null

        fun getInstance(context: Context): NewStoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: NewStoryViewModelFactory(
                    Injection.storyRepository(context),
                )
            }.also { instance = it }
    }
}