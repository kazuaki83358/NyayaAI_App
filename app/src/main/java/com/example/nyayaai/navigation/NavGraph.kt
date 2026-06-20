package com.example.nyayaai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nyayaai.ui.screens.chat.ChatScreen
import com.example.nyayaai.ui.screens.documents.DocumentsScreen
import com.example.nyayaai.ui.screens.home.HomeScreen
import com.example.nyayaai.ui.screens.profile.ProfileScreen

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.nyayaai.data.PreferenceManager
import com.example.nyayaai.ui.screens.login.LoginScreen
import com.example.nyayaai.ui.screens.signup.SignUpScreen
import com.example.nyayaai.ui.screens.choose.ChooseRoleScreen
import com.example.nyayaai.ui.screens.lawerdashboard.LawyerDashboardScreen
import kotlinx.coroutines.launch

import com.example.nyayaai.ui.screens.documents.FindLawyerScreen
import com.example.nyayaai.ui.screens.profile.LawyerProfileScreen
import com.example.nyayaai.ui.screens.lawerdashboard.LawyerRequestsScreen
import com.example.nyayaai.ui.screens.notifications.NotificationScreen
import com.example.nyayaai.ui.screens.chat.ChatDetailScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val preferenceManager = PreferenceManager(context)
    val isLoggedIn = preferenceManager.isLoggedIn.collectAsState(initial = null)
    val userRole = preferenceManager.userRole.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    if (isLoggedIn.value == null) {
        // Simple loading or blank screen to prevent flicker
        return
    }

    val startDestination = if (isLoggedIn.value == true) {
        when (userRole.value) {
            "lawyer" -> Screen.LawyerDashboard.route
            "common_man" -> Screen.Home.route
            "pending" -> Screen.ChooseRole.route
            else -> Screen.Home.route // Fallback
        }
    } else {
        Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    scope.launch {
                        preferenceManager.setLoggedIn(true)
                        preferenceManager.setUserRole(role)
                        val destination = when (role) {
                            "lawyer" -> Screen.LawyerDashboard.route
                            "common_man" -> Screen.Home.route
                            else -> Screen.ChooseRole.route
                        }
                        navController.navigate(destination) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.ChooseRole.route)
                },
                onSignInClick = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
        composable(Screen.ChooseRole.route) {
            ChooseRoleScreen(
                onUserClick = {
                    scope.launch {
                        preferenceManager.setUserRole("common_man")
                        preferenceManager.setLoggedIn(true)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.ChooseRole.route) { inclusive = true }
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    }
                },
                onLawyerClick = {
                    scope.launch {
                        preferenceManager.setUserRole("lawyer")
                        preferenceManager.setLoggedIn(true)
                        navController.navigate(Screen.LawyerDashboard.route) {
                            popUpTo(Screen.ChooseRole.route) { inclusive = true }
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Chat.route) {
            ChatScreen(navController)
        }
        composable(Screen.Documents.route) {
            if (userRole.value == "lawyer") {
                // Lawyers might still want documents or something else, but for now let's keep it consistent
                DocumentsScreen(navController)
            } else {
                FindLawyerScreen(navController)
            }
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
        composable(Screen.LawyerDashboard.route) {
            LawyerDashboardScreen(navController)
        }
        composable(Screen.LawyerProfile.route) {
            LawyerProfileScreen(navController)
        }
        composable(Screen.LawyerRequests.route) {
            LawyerRequestsScreen(navController)
        }
        composable(Screen.Notifications.route) {
            NotificationScreen(navController)
        }
        composable(Screen.ChatDetail.route) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
            ChatDetailScreen(navController, requestId)
        }
    }
}
