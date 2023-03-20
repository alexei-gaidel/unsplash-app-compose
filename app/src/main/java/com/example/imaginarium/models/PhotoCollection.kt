package com.example.imaginarium.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoCollection(
    val id: String,
    val title: String,
    @Json(name = "total_photos")
    val totalPhotos: Long,
//val private: Boolean,
    @Json(name = "cover_photo")
    val coverPhoto: CoverPhoto,
    val user: CollectionOwner,
)

@JsonClass(generateAdapter = true)
data class CoverPhoto(
    val urls: CoverUrls
)

@JsonClass(generateAdapter = true)
data class CoverUrls(
    val regular: String,
    val small: String,
)

@JsonClass(generateAdapter = true)
data class CollectionOwner(
    val id: String,
    val username: String,
    val name: String,
    @Json(name = "profile_image")
    val profileImage: UserProfileImage,
)

@JsonClass(generateAdapter = true)
data class UserProfileImage(
    val small: String,
    val medium: String,
    val large: String
)





