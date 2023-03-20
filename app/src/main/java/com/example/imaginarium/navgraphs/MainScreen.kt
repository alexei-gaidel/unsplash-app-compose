package com.example.imaginarium.navgraphs

import androidx.annotation.DrawableRes
import com.example.imaginarium.R

sealed class MainScreen(val route: String, @DrawableRes val resourceId: Int) {
    object Photos : MainScreen("home", R.drawable.photo_icon)
    object Collections : MainScreen("collections", R.drawable.collections_icon)
    object Profile : MainScreen("profile", R.drawable.person)
}
