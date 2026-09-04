package com.example.trashnetclassifier.presentation.main

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val route: String) {
    @Serializable
    object Home : Screen("home")

    @Serializable
    object Capture : Screen("capture")

    @Serializable
    object History : Screen("history")
}
