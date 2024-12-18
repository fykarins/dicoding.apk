package com.example.storyapp.data.remote

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.storyapp.data.local.AppEntity
import com.example.storyapp.data.local.ListStoryItem
import com.example.storyapp.data.local.StoryDatabase

@OptIn(ExperimentalPagingApi::class)
class StoryRetrofit(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val token: String
) : RemoteMediator<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val response =
                apiService.getAllStories("Bearer $token", page, state.config.pageSize)


            val endOfPaginationReached = response.listStory.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    Log.d("RemoteMediator", "Clearing remote keys and stories in the database")
                    database.appDAO().deleteAppKeys()
                    database.storyDAO().deleteAll()
                }

                val keys = response.listStory.map { story ->
                    AppEntity(
                        id = story.id,
                        prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                        nextKey = if (endOfPaginationReached) null else page + 1
                    )
                }

                database.appDAO().insertAll(keys)

                database.storyDAO().insertStory(response.listStory.map {
                    ListStoryItem(
                        photoUrl = it.photoUrl,
                        createdAt = it.createdAt,
                        name = it.name,
                        description = it.description,
                        lon = it.lon as Double?,
                        id = it.id,
                        lat = it.lat as Double?
                    )
                })
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }

    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStoryItem>): AppEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { story ->
            database.appDAO().getAppEntityId(story.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStoryItem>): AppEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { story ->
            database.appDAO().getAppEntityId(story.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListStoryItem>): AppEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.appDAO().getAppEntityId(id)
            }
        }
    }
}
