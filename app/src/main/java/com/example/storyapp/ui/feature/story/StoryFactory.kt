package com.example.storyapp.ui.feature.story

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.storyapp.R
import com.example.storyapp.data.remote.ListStoryItem
import com.example.storyapp.data.story.StoryRepository
import com.example.storyapp.data.story.StoryResult
import com.example.storyapp.data.user.UserPreferences
import com.example.storyapp.di.Injection
import com.squareup.picasso.Picasso
import kotlinx.coroutines.runBlocking

class StoryFactory(private val context: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val storyList = mutableListOf<ListStoryItem>()
    private val storyRepository: StoryRepository = Injection.storyRepository(context)

    override fun onCreate() {
        runBlocking {
            val token = UserPreferences(context).getToken()
            if (token != null) {
                val result =
                    storyRepository.getAllStories(token, page = null, size = null, location = 0)
                when (result) {
                    is StoryResult.Success -> {
                        storyList.clear()
                        storyList.addAll(result.data.listStory)
                        Log.d("StoryRemoteViewsFactory", "Data retrieved successfully: ${storyList.size}")
                    }

                    is StoryResult.Error -> {
                        storyList.clear()
                        Log.e("StoryRemoteViewsFactory", "Failed to retrieve data: ${result.error}")
                    }

                    is StoryResult.Loading -> {}
                }
            } else {
                storyList.clear()
                Log.d("StoryRemoteViewsFactory", "Token not found, data cleared.")
            }
        }
    }

    override fun onDataSetChanged() {
        Log.d("StoryRemoteViewsFactory", "onDataSetChanged called")
        runBlocking {
            val token = UserPreferences(context).getToken()
            if (token != null) {
                val result =
                    storyRepository.getAllStories(token, page = null, size = null, location = 0)
                when (result) {
                    is StoryResult.Success -> {
                        storyList.clear()
                        storyList.addAll(result.data.listStory)
                        Log.d("StoryRemoteViewsFactory", "Data retrieved successfully: ${storyList.size}")
                    }

                    is StoryResult.Error -> {
                        storyList.clear()
                        Log.e("StoryRemoteViewsFactory", "Failed to retrieve data: ${result.error}")
                    }

                    is StoryResult.Loading -> {}
                }
            } else {
                storyList.clear()
                Log.d("StoryRemoteViewsFactory", "Token not found, data cleared.")
            }
        }
    }

    override fun onDestroy() {
        storyList.clear()
    }

    override fun getCount(): Int {
        return storyList.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.story_app)

        if (storyList.isEmpty()) {
            views.setViewVisibility(R.id.empty_view, View.VISIBLE)
            views.setViewVisibility(R.id.stack_view, View.GONE)
            return views
        }

        views.setViewVisibility(R.id.empty_view, View.GONE)
        views.setViewVisibility(R.id.stack_view, View.VISIBLE)

        val story = storyList[position]
        val itemViews = RemoteViews(context.packageName, R.layout.story_item)

        val imageUrl = story.photoUrl
        val bitmap = getBitmapFromUrl(context, imageUrl)

        if (bitmap != null) {
            itemViews.setImageViewBitmap(R.id.imageView, bitmap)
        } else {
            itemViews.setImageViewResource(R.id.imageView, R.drawable.ic_placeholder)
        }

        return itemViews
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}

@Suppress("UNUSED_PARAMETER")
private fun getBitmapFromUrl(context: Context, url: String): Bitmap? {
    return try {
        Picasso.get()
            .load(url)
            .get()
    } catch (e: Exception) {
        null
    }
}