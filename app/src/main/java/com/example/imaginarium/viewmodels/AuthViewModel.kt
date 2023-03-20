package com.example.imaginarium.viewmodels

import android.app.Application
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.imaginarium.R
import com.example.imaginarium.auth.authorization.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application, private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    private val authService: AuthorizationService = AuthorizationService(getApplication())
    private val openAuthPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)
    private val authSuccessEventChannel = Channel<Unit>(Channel.BUFFERED)

    val openAuthPageFlow: Flow<Intent>
        get() = openAuthPageEventChannel.receiveAsFlow()

    val authSuccessFlow: Flow<Unit>
        get() = authSuccessEventChannel.receiveAsFlow()

    fun onAuthCodeFailed(exception: AuthorizationException) {
        toastEventChannel.trySendBlocking(R.string.auth_canceled)
    }

    fun onAuthCodeReceived(tokenRequest: TokenRequest) {

        viewModelScope.launch {
            runCatching {
                authRepository.performTokenRequest(
                    authService = authService, tokenRequest = tokenRequest
                )
            }.onSuccess {
                authSuccessEventChannel.send(Unit)
            }
        }
    }

    fun openLoginPage() {

        val customTabsIntent = CustomTabsIntent.Builder().build()
        val authRequest = authRepository.getAuthRequest()
        val openAuthPageIntent = authService.getAuthorizationRequestIntent(
            authRequest, customTabsIntent
        )
        openAuthPageEventChannel.trySendBlocking(openAuthPageIntent)

    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }

}