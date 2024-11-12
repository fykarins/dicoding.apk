package com.example.storyapp.ui.feature.factory

import android.content.Context

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.story.StoryRepository
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.feature.story.StoryMapViewModel
import kotlin.Suppress
import kotlin.jvm.Volatile

class StoryMapViewModelFactory(

    private val storyRepository: StoryRepository,

    ) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryMapViewModel::class.java)) {
            return StoryMapViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: StoryMapViewModelFactory? = null

        fun getInstance(context: Context): StoryMapViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: StoryMapViewModelFactory(
                    Injection.storyRepository(context),
                )
            }.also { instance = it }
    }
}