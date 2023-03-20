package com.example.imaginarium.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoDetails(
    val id: String?,
    val downloads: Long?,
    val likes: Long?,
    @Json(name = "liked_by_user")
    val likedByUser: Boolean,
    val exif: Exif?,
    val location: Location?,
    val tags: List<Tags?>,
    val urls: Urls?,
    val user: Author?
)

@JsonClass(generateAdapter = true)
data class CurrentUserCollections(
    val id: Long?,
    val title: String?,
    @Json(name = "published_at")
    val publishedAt: String?,
    @Json(name = "last_collected_at")
    val lastCollectedAt: String?,
    @Json(name = "updated_at")
    val updatedAt: String?,
    val user: Any? = null
)

@JsonClass(generateAdapter = true)
data class Exif(
    val make: String?,
    val model: String?,
    val name: String?,
    @Json(name = "exposure_time")
    val exposureTime: String?,
    val aperture: String?,
    @Json(name = "focal_length")
    val focalLength: String?,
    val iso: Long?
)

@JsonClass(generateAdapter = true)
data class Location(
    val city: String?,
    val country: String?,
    val position: Position?
)

@JsonClass(generateAdapter = true)
data class Position(
    val latitude: Double?,
    val longitude: Double?
)

@JsonClass(generateAdapter = true)
data class Tags(
    val title: String?
)

@JsonClass(generateAdapter = true)
data class Author(
    val id: String?,
    val username: String?,
    val name: String?,
    val bio: String?,
    @Json(name = "total_photos")
    val totalPhotos: Long?,
    @Json(name = "total_collections")
    val totalCollections: Long?
)



