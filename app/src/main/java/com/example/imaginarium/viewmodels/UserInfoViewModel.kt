package com.example.imaginarium.viewmodels

import android.app.Application
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.imaginarium.auth.authorization.AuthRepository
import com.example.imaginarium.models.UnsplashUser
import com.example.imaginarium.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationService
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    application: Application,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : AndroidViewModel(application) {

    private val authService: AuthorizationService = AuthorizationService(getApplication())
    private val logoutMutableStateFlow = MutableStateFlow(false)
    private val loadingMutableStateFlow = MutableStateFlow(false)
    private val userInfoMutableStateFlow = MutableStateFlow<UnsplashUser?>(null)
    private val logoutPageEventChannel = Channel<Intent>(Channel.BUFFERED)

    private val logoutCompletedEventChannel = Channel<Intent>(Channel.BUFFERED)
    val logoutFlow: Flow<Boolean>
        get() = logoutMutableStateFlow.asStateFlow()

    val userInfoFlow: Flow<UnsplashUser?>
        get() = userInfoMutableStateFlow.asStateFlow()

    val logoutPageFlow: Flow<Intent>
        get() = logoutPageEventChannel.receiveAsFlow()

    val logoutCompletedFlow: Flow<Intent>
        get() = logoutCompletedEventChannel.receiveAsFlow()

    fun loadUserInfo() {
        viewModelScope.launch {
            loadingMutableStateFlow.value = true
            runCatching {
                userRepository.getUserInformation()
            }.onSuccess {
                userInfoMutableStateFlow.value = it
                loadingMutableStateFlow.value = false
            }.onFailure {
                loadingMutableStateFlow.value = false
            }
        }
    }

    fun logout() {

        val customTabsIntent = CustomTabsIntent.Builder().build()
        val logoutPageIntent = authService.getEndSessionRequestIntent(
            authRepository.getEndSessionRequest(), customTabsIntent
        )
        viewModelScope.launch {
            kotlin.runCatching { logoutPageEventChannel.send(logoutPageIntent) }.onSuccess {
                    logoutMutableStateFlow.value = true
                }
        }
    }

    fun webLogoutComplete() {
        authRepository.logout()

        val customTabsIntent = CustomTabsIntent.Builder().build()
        val authRequest = authRepository.getNewAuthRequest()
        val openAuthPageIntent = authService.getAuthorizationRequestIntent(
            authRequest, customTabsIntent
        )
        logoutCompletedEventChannel.trySendBlocking(openAuthPageIntent)
    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }
}