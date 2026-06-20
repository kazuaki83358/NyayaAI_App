package com.example.nyayaai.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ChooseRole : Screen("choose_role")
    object Home : Screen("home") // Common Man Home
    object Chat : Screen("chat")
    object Documents : Screen("documents") // Law Library / Find Lawyer
    object Profile : Screen("profile")
    object LawyerDashboard : Screen("lawyer_dashboard")
    object LawyerProfile : Screen("lawyer_profile")
    object LawyerRequests : Screen("lawyer_requests")
    object Notifications : Screen("notifications")
    object ChatDetail : Screen("chat_detail/{requestId}") {
        fun createRoute(requestId: String) = "chat_detail/$requestId"
    }
}
