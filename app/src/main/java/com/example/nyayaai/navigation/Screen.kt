package com.example.nyayaai.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Chat : Screen("chat")
    object Documents : Screen("documents")
    object Profile : Screen("profile")
}