package com.yogaprasetyo.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.yogaprasetyo.storyapp.data.remote.response.ListStoryItem
import com.yogaprasetyo.storyapp.data.remote.retrofit.ApiService


/**
 * Paging3
 *
 * Handle retrieve data from server until last page
 * Data will automatically changing if move to next page
 * */
class StoryPagingSource(private val apiService: ApiService, private val token: String) :
    PagingSource<Int, ListStoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.allStories(
                authorization = token,
                page = page,
                size = params.loadSize
            )
            val data = response.listStory

            LoadResult.Page(
                data = data,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (data.isNullOrEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}