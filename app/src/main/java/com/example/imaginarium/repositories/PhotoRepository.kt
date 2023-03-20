package com.example.imaginarium.repositories

import com.example.imaginarium.auth.network.Networking
import com.example.imaginarium.models.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor() {

    suspend fun getPhotos(page: Int): List<PhotoNetworkEntity> {
        val photos = Networking.unsplashApi.getPhotos("popular", page)
        val entities: List<PhotoNetworkEntity> = photos.map { photo ->
            PhotoNetworkEntity(
                id = photo.id,
                likes = photo.likes,
                likedByUser = photo.likedByUser,
                username = photo.user.username,
                uri = photo.urls.regular,
                user = photo.user.name,
                profileImage = photo.user.profileImage.medium
            )
        }
        return entities
    }

    suspend fun searchPhotos(query: String): List<PhotoItem> {
        return Networking.unsplashApi.searchPhotos(query).results
    }

    suspend fun getLikedPhotos(username: String, pageSize: Int): List<PhotoItem> {
        return Networking.unsplashApi.getLikedPhotos(username, pageSize)
    }

    suspend fun getCollections(): List<PhotoCollection> {
        return Networking.unsplashApi.getCollections()
    }

    suspend fun getCollectionPhotos(id: String): List<PhotoItem> {
        return Networking.unsplashApi.getCollectionPhotos(id)
    }

    suspend fun getSinglePhoto(id: String): PhotoDetails {
        return Networking.unsplashApi.getSinglePhoto(id)
    }

    suspend fun getSingleCollection(id: String): CollectionInfo {
        return Networking.unsplashApi.getSingleCollection(id)
    }

    suspend fun getPhotoLiked(id: String): PhotoLikes {
        return Networking.unsplashApi.likePhoto(id)
    }

    suspend fun getPhotoUnliked(id: String): PhotoLikes {
        return Networking.unsplashApi.unlikePhoto(id)
    }
}