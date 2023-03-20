package com.example.imaginarium.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.imaginarium.models.PhotoCollection

class CollectionDataSource : PagingSource<Int, PhotoCollection>() {

    private val repository = PhotoRepository()
    override fun getRefreshKey(state: PagingState<Int, PhotoCollection>): Int? = FIRST_PAGE
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoCollection> {

        return kotlin.runCatching {
            repository.getCollections()
        }.fold(onSuccess = {
            LoadResult.Page(
                data = it, prevKey = params.key?.let { it - 1 }, nextKey = (params.key ?: 0) + 1
            )
        }, onFailure = {
            LoadResult.Error(it)
        })
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}