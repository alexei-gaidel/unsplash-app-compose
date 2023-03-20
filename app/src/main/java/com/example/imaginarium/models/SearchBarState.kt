package com.example.imaginarium.models

sealed class SearchBarState {
    object Opened : SearchBarState()
    object Closed : SearchBarState()
}
