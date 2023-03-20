package com.example.imaginarium.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UnsplashUser(
    val id: String,
    val username: String,
    val name: String,
    @Json(name = "twitter_username") val twitterUsername: String,
    val bio: String,
    val location: String?,
    val email: String,
    val downloads: Long,
    @Json(name = "profile_image") val profileImage: ProfileImage
)

@JsonClass(generateAdapter = true)
data class UnsplashUser1(
    val id: String,
    val username: String,
    val name: String,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String,
    @Json(name = "twitter_username") val twitterUsername: String,
    val bio: String,
    val location: String,
    @Json(name = "email")val email: String,
    @Json(name = "total_likes") val totalLikes: Long,
    @Json(name = "total_photos") val totalPhotos: Long,
    @Json(name = "total_collections") val totalCollections: Long,
    val downloads: Long,
    @Json(name = "profile_image") val profileImage: ProfileImage,
)

@JsonClass(generateAdapter = true)
data class ProfileImage(
    val small: String, val medium: String, val large: String
)

@JsonClass(generateAdapter = true)
data class Social(
    @Json(name = "instagram_username") val instagramUsername: String,
    @Json(name = "portfolio_url") val portfolioURL: String,
    @Json(name = "twitter_username") val twitterUsername: String
)
