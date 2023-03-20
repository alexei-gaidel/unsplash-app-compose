package com.example.imaginarium.viewmodels

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.imaginarium.R
import com.example.imaginarium.database.PhotoDao
import com.example.imaginarium.models.*
import com.example.imaginarium.repositories.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    application: Application,
    private val remoteMediator: PhotoRemoteMediator,
    private val photoDao: PhotoDao
) : AndroidViewModel(application) {

    val searchBarMutableState: MutableState<SearchBarState> = mutableStateOf(SearchBarState.Closed)
    val searchTextMutableState: MutableState<String> = mutableStateOf("")
    var startSnackbar = MutableStateFlow(false)
    private val photoRepository = PhotoRepository()
    private val photoDetailsMutableFlow = MutableStateFlow<PhotoDetails?>(null)
    private val collectionInfoMutableFlow = MutableStateFlow<CollectionInfo?>(null)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)
    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading = _isLoading

    val uriFlow = MutableStateFlow<String?>("")
    val searchBarState: State<SearchBarState> = searchBarMutableState
    val searchTextState: State<String> = searchTextMutableState
    val photoDetailsFlow: Flow<PhotoDetails?>
        get() = photoDetailsMutableFlow.asStateFlow()
    val collectionInfoFlow: Flow<CollectionInfo?>
        get() = collectionInfoMutableFlow.asStateFlow()

    fun updateSearchBarState(newValue: SearchBarState) {
        searchBarMutableState.value = newValue
    }

    fun updateSearchTextState(newValue: String) {
        searchTextMutableState.value = newValue
    }

    fun pagingSearchPhotos(query: String): Flow<PagingData<PhotoItem>> {
        return Pager(config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { SearchResultDataSource(query) }).flow.cachedIn(viewModelScope)
    }

    fun pagingCollectionsPhotos(id: String): Flow<PagingData<PhotoItem>> {
        return Pager(config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { CollectionsPhotoDataSource(id) }).flow.cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalPagingApi::class)
    fun pagingPhotos(): Flow<PagingData<PhotoNetworkEntity>> {

        return Pager(
            config = PagingConfig(pageSize = 10, initialLoadSize = 10),
            pagingSourceFactory = { photoDao.getPagingSource() },
            remoteMediator = remoteMediator
        ).flow.map { pagingdata ->
                pagingdata.map {
                    PhotoNetworkEntity(
                        it.id,
                        it.likes,
                        it.likedByUser,
                        it.uri,
                        it.username,
                        it.user,
                        it.profileImage
                    )
                }
            }.cachedIn(viewModelScope)
    }

    fun pagingCollections(): Flow<PagingData<PhotoCollection>> {
        return Pager(config = PagingConfig(pageSize = 10, initialLoadSize = 16),
            pagingSourceFactory = { CollectionDataSource() }).flow.cachedIn(viewModelScope)
    }

    fun pagingLikedPhotos(username: String): Flow<PagingData<PhotoItem>> {
        val pageSize = 6
        return Pager(config = PagingConfig(pageSize = pageSize, initialLoadSize = 12),
            pagingSourceFactory = { LikedPhotosDataSource(username, pageSize) }).flow.cachedIn(
            viewModelScope
        )
    }

    fun getSingleCollection(id: String): Flow<CollectionInfo?> {
        viewModelScope.launch {
            kotlin.runCatching {
                photoRepository.getSingleCollection(id)
            }.onSuccess {
                collectionInfoMutableFlow.value = it

            }.onFailure {
                toastEventChannel.trySendBlocking(R.string.get_single_photo_error)
            }
        }
        return collectionInfoFlow
    }

    fun getSinglePhoto(id: String): Flow<PhotoDetails?> {
        viewModelScope.launch {
            kotlin.runCatching {
                photoRepository.getSinglePhoto(id)
            }.onSuccess {
                photoDetailsMutableFlow.value = it

            }.onFailure {
                toastEventChannel.trySendBlocking(R.string.get_single_photo_error)
            }
        }
        return photoDetailsFlow
    }

    fun getPhotoLiked(id: String): PhotoLikes? {
        var photoLikesDto: PhotoLikes? = null
        viewModelScope.launch {
            try {
                photoLikesDto = photoRepository.getPhotoLiked(id)
            } catch (e: Exception) {
                e.message
            }
        }
        return photoLikesDto
    }

    fun getPhotoUnliked(id: String): PhotoLikes? {
        var photoLikesDto: PhotoLikes? = null
        viewModelScope.launch {
            try {
                photoLikesDto = photoRepository.getPhotoUnliked(id)

            } catch (e: Exception) {
                e.message
            }
        }
        return photoLikesDto
    }

}