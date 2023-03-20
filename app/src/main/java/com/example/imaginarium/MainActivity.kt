package com.example.imaginarium

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.imaginarium.auth.authorization.TokenStorage
import com.example.imaginarium.compose.Main
import com.example.imaginarium.compose.OnBoarding
import com.example.imaginarium.download.DownloadCompletedReceiver
import com.example.imaginarium.navgraphs.InitScreen
import com.example.imaginarium.ui.theme.ImaginariumTheme
import com.example.imaginarium.viewmodels.AuthViewModel
import com.example.imaginarium.viewmodels.PhotoViewModel
import com.example.imaginarium.viewmodels.UserInfoViewModel
import com.kts.github.utils.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse


@AndroidEntryPoint
class MainActivity : ComponentActivity(), DownloadCompletedReceiver.DownloadInterface {

    private var isOnBoarded = false
    private var isAuthorized = false
    private val photoViewModel: PhotoViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val userInfoViewModel: UserInfoViewModel by viewModels()
    private val receiver = DownloadCompletedReceiver()
    private val getAuthResponse =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val dataIntent = it.data ?: return@registerForActivityResult
            handleAuthResponseIntent(dataIntent)
        }
    private val logoutResponse = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        userInfoViewModel.webLogoutComplete()
    }

    @SuppressLint(
        "StateFlowValueCalledInComposition",
        "SuspiciousIndentation",
        "CoroutineCreationDuringComposition",
        "UnusedMaterialScaffoldPaddingParameter",
        "Range"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        TokenStorage.accessToken = sharedPref.getString("accessToken", null)

        installSplashScreen().setKeepOnScreenCondition {
            !photoViewModel.isLoading.value
        }

        receiver.registerReceiver(this)
        val filter = IntentFilter()
        filter.addAction("android.intent.action.DOWNLOAD_COMPLETE")
        registerReceiver(receiver, filter)
        photoViewModel.startSnackbar.value = false
        setContent {

            ImaginariumTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {

                }

                Initialization()
            }
        }
    }

    override fun updateDownloaded() {
        photoViewModel.startSnackbar.value = true
        photoViewModel.uriFlow.value = receiver.savedUri
    }

    @Composable
    fun Initialization() {

        val navController = rememberNavController()

        if (TokenStorage.accessToken != null) {
            isAuthorized = true
            isOnBoarded = true
        }

        val startDestination =
            if (!isOnBoarded) InitScreen.OnBoarding.route else if (!isAuthorized) InitScreen.Authorization.route else InitScreen.Main.route
        NavHost(
            navController = navController, startDestination = startDestination
        ) {
            composable(InitScreen.OnBoarding.route) { OnBoarding(navController) }
            composable(InitScreen.Authorization.route) {
                Authorization(navController = navController)
            }
            composable(InitScreen.Main.route) {
                Main(photoViewModel = photoViewModel, userInfoViewModel = userInfoViewModel)
            }
        }

        val loggedOutState = userInfoViewModel.logoutFlow.collectAsState(initial = false).value
        if (loggedOutState) {
            navController.popBackStack()
            initializeLogOutFlows(LocalLifecycleOwner.current, navController)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun Authorization(navController: NavController) {
        photoViewModel.isLoading.value = false
        authViewModel.openLoginPage()
        initializeAuthFlows(LocalLifecycleOwner.current, navController)
    }

    private fun openAuthPage(intent: Intent) {
        getAuthResponse.launch(intent)
    }

    fun initializeAuthFlows(lifecycleOwner: LifecycleOwner, navController: NavController) {
        authViewModel.openAuthPageFlow.launchAndCollectIn(lifecycleOwner) {
            openAuthPage(it)
            isAuthorized = true
            isOnBoarded = true
        }

        authViewModel.authSuccessFlow.launchAndCollectIn(lifecycleOwner) {
            navController.popBackStack()
            if (TokenStorage.accessToken != null) {
                navController.navigate(InitScreen.Main.route)
            }
        }
    }


    fun initializeLogOutFlows(lifecycleOwner: LifecycleOwner, navController: NavController) {
        userInfoViewModel.logoutPageFlow.launchAndCollectIn(lifecycleOwner) {
            logoutResponse.launch(it)
        }

        userInfoViewModel.logoutCompletedFlow.launchAndCollectIn(lifecycleOwner) {
            val sharedPref = getPreferences(Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()
            navController.popBackStack()
            navController.navigate(InitScreen.Authorization.route)

        }

    }

    private fun handleAuthResponseIntent(intent: Intent) {
        val exception = AuthorizationException.fromIntent(intent)
        val tokenExchangeRequest =
            AuthorizationResponse.fromIntent(intent)?.createTokenExchangeRequest()
        when {
            exception != null -> authViewModel.onAuthCodeFailed(exception)
            tokenExchangeRequest != null -> authViewModel.onAuthCodeReceived(tokenExchangeRequest)
        }
    }

    override fun onStop() {
        super.onStop()
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean("isOnBoarded", isOnBoarded)
            putString("accessToken", TokenStorage.accessToken)
            apply()
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        isOnBoarded = sharedPref.getBoolean("isOnBoarded", false)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

}

