package com.example.imaginarium.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.imaginarium.database.AppDatabase
import com.example.imaginarium.database.PhotoDBEntity
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class PhotoRemoteMediator @Inject constructor(
    private val repository: PhotoRepository, private val database: AppDatabase
) : RemoteMediator<Int, PhotoDBEntity>() {

    private var pageIndex = 1
    val photoDao = database.photoDao()
    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, PhotoDBEntity>
    ): MediatorResult {
        return try {
            pageIndex = getPageIndex(loadType) ?: return MediatorResult.Success(
                endOfPaginationReached = true
            )
            when (loadType) {
                LoadType.REFRESH -> {
                    null
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull() ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    lastItem.id + 1
                }
            }

            val photos = repository.getPhotos(pageIndex).map {
                PhotoDBEntity(
                    it.id, it.likes, it.likedByUser, it.uri, it.username, it.user, it.profileImage
                )
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    photoDao.delete()
                }
                photoDao.save(photos)
            }
            MediatorResult.Success(endOfPaginationReached = photos.isNullOrEmpty())
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private fun getPageIndex(loadType: LoadType): Int? {
        pageIndex = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return null
            LoadType.APPEND -> ++pageIndex
        }
        return pageIndex
    }


}