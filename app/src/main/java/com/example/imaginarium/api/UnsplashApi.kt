package com.example.imaginarium.api

import com.example.imaginarium.models.*
import retrofit2.http.*

interface UnsplashApi {
    @GET("me")
    suspend fun getCurrentUser(
    ): UnsplashUser

    @GET("photos")
    suspend fun getPhotos(
        @Query("orderBy") orderBy: String,
        @Query("page") page: Int
    ): List<PhotoItem>

    @GET("/users/{username}/likes")
    suspend fun getLikedPhotos(
        @Path("username") userName: String,
        @Query("per_page") pageSize: Int
    ): List<PhotoItem>

    @GET("search/photos")
    suspend fun searchPhotos(@Query("query") query: String): SearchResults

    @GET("/collections")
    suspend fun getCollections(): List<PhotoCollection>

    @GET("/collections/{id}")
    suspend fun getSingleCollection(@Path("id") id: String): CollectionInfo

    @GET("/collections/{id}/photos")
    suspend fun getCollectionPhotos(@Path("id") id: String): List<PhotoItem>

    @GET("/photos/{id}")
    suspend fun getSinglePhoto(@Path("id") id: String): PhotoDetails

    @POST("/photos/{id}/like")
    suspend fun likePhoto(@Path("id") id: String): PhotoLikes

    @DELETE("/photos/{id}/like")
    suspend fun unlikePhoto(@Path("id") id: String): PhotoLikes

}
