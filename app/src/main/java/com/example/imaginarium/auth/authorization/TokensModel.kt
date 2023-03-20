package com.example.imaginarium.auth.authorization

data class TokensModel(
    val accessToken: String,
    val refreshToken: String,
    val idToken: String
)
