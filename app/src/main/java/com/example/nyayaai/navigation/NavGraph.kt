package com.example.nyayaai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nyayaai.ui.screens.chat.ChatScreen
import com.example.nyayaai.ui.screens.documents.DocumentsScreen
import com.example.nyayaai.ui.screens.home.HomeScreen
import com.example.nyayaai.ui.screens.profile.ProfileScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Chat.route) {
            ChatScreen(navController)
        }
        composable(Screen.Documents.route) {
            DocumentsScreen(navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
    }
}