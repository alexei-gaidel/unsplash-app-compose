package com.example.imaginarium.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.imaginarium.models.PhotoItem

class CollectionsPhotoDataSource(val id: String) : PagingSource<Int, PhotoItem>() {

    private val repository = PhotoRepository()
    override fun getRefreshKey(state: PagingState<Int, PhotoItem>): Int? = FIRST_PAGE
    override suspend fun load(params: PagingSource.LoadParams<Int>): PagingSource.LoadResult<Int, PhotoItem> {

        return kotlin.runCatching {
            repository.getCollectionPhotos(id = id)
        }.fold(
            onSuccess = {
                PagingSource.LoadResult.Page(
                    data = it,
                    prevKey = params.key?.let { it - 1 },
                    nextKey = (params.key ?: 0) + 1
                )
            },
            onFailure = {
                PagingSource.LoadResult.Error(it)
            }
        )
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}