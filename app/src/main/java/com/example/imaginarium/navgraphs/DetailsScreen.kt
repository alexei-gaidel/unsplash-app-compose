package com.example.imaginarium.navgraphs

sealed class DetailsScreen(val route: String) {
    object SinglePhoto : DetailsScreen("singlephoto")
    object SingleCollection : DetailsScreen("singlecollection")
    object Collections : DetailsScreen("collections")
    object SearchView: DetailsScreen("searchview")
    object SinglePhotoDeepLink: DetailsScreen("singledeeplink")
}
