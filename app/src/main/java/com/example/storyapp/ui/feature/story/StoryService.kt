package com.example.storyapp.ui.feature.story

import android.content.Intent
import android.widget.RemoteViewsService

class StoryService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return StoryFactory(applicationContext)
    }
}