package com.example.imaginarium.auth.authorization

import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.TokenRequest
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {


    fun corruptAccessToken() {
        TokenStorage.accessToken = "fake token"
    }

    fun logout() {
        TokenStorage.accessToken = null
        TokenStorage.refreshToken = null
        TokenStorage.idToken = null
        Timber.tag("Oauth").d("logout token is ${TokenStorage.accessToken}")
    }

    fun getAuthRequest(): AuthorizationRequest {
        Timber.tag("Oauth").d("GetAuthREquest in AuthRepo")
        return AppAuth.getAuthRequest()
    }

    fun getNewAuthRequest(): AuthorizationRequest {
        Timber.tag("Oauth").d("GetAuthREquest in AuthRepo")
        return AppAuth.getNewAuthRequest()

    }

    fun getEndSessionRequest(): EndSessionRequest {
        Timber.tag("Oauth").d("GetEndSessionREquest in AuthRepo")

        return AppAuth.getEndSessionRequest()
    }

    suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    ) {
        val tokens = AppAuth.performTokenRequestSuspend(authService, tokenRequest)
        TokenStorage.accessToken = tokens.accessToken
        TokenStorage.refreshToken = tokens.refreshToken
        TokenStorage.idToken = tokens.idToken
        Timber.tag("Oauth")
            .d("6. Tokens accepted:\n access=${tokens.accessToken}\nrefresh=${tokens.refreshToken}\nidToken=${tokens.idToken}")
    }
}