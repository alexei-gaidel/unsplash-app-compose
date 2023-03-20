package com.example.imaginarium.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoItem(
    val id: String,
    val likes: Long,
    @Json(name = "liked_by_user")
    val likedByUser: Boolean,
    val user: User,
    val urls: Urls
)

@JsonClass(generateAdapter = true)
data class Urls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
)

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val username: String,
    val name: String,
    @Json(name = "profile_image")
    val profileImage: ProfileImage,

    )

@JsonClass(generateAdapter = true)
data class SearchResults(
val total: Long,
@Json(name = "total_pages") val totalPages: Long,
val results: List<PhotoItem>)



