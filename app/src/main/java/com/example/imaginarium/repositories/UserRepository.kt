package com.example.imaginarium.repositories

import com.example.imaginarium.auth.network.Networking
import com.example.imaginarium.models.UnsplashUser
import javax.inject.Inject

class UserRepository @Inject constructor() {
    suspend fun getUserInformation(): UnsplashUser {
        return Networking.unsplashApi.getCurrentUser()
    }
}