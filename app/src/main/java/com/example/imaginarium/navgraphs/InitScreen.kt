package com.example.imaginarium.navgraphs

import androidx.annotation.DrawableRes
import com.example.imaginarium.R

sealed class InitScreen(val route: String) {
    object OnBoarding : InitScreen("onboarding")
    object Authorization : InitScreen("authorization")
    object Main : InitScreen("main")
}
