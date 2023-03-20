package com.example.imaginarium.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.imaginarium.models.PhotoItem

class LikedPhotosDataSource(val username: String, val pageSize: Int) :
    PagingSource<Int, PhotoItem>() {

    private val repository = PhotoRepository()
    override fun getRefreshKey(state: PagingState<Int, PhotoItem>): Int? = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoItem> {

        val page = params.key ?: 1

        return kotlin.runCatching {
            repository.getLikedPhotos(username, pageSize)
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it,
                    prevKey = null,
                    nextKey = if (it.isEmpty()) null else page + 1
                )
            },
            onFailure = {
                LoadResult.Error(it)
            }
        )
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}
