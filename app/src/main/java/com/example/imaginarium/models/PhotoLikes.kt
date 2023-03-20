package com.example.imaginarium.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoLikes(
    val photo: Photo,
)

@JsonClass(generateAdapter = true)
data class Photo(
    val id: String,
    val likes: Long,
    @Json(name = "liked_by_user")
    val likedByUser: Boolean,
)

