package com.example.imaginarium.models

data class PhotoNetworkEntity(
    val id: String,
    val likes: Long,
    val likedByUser: Boolean,
    var uri: String,
    val username: String,
    val user: String,
    val profileImage: String

)
