package com.example.nyayaai.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nyayaai.navigation.Screen
import com.example.nyayaai.ui.theme.BrandIndigo

import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.nyayaai.data.PreferenceManager

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val preferenceManager = PreferenceManager(context)
    val userRole = preferenceManager.userRole.collectAsState(initial = "common_man")

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val items = if (userRole.value == "lawyer") {
            listOf(
                Triple(Screen.LawyerDashboard, "Dashboard", Icons.Default.Dashboard),
                Triple(Screen.LawyerRequests, "Requests", Icons.Default.Notifications),
                Triple(Screen.Chat, "AI Chat", Icons.Default.ChatBubbleOutline),
                Triple(Screen.Profile, "Profile", Icons.Default.PersonOutline)
            )
        } else {
            listOf(
                Triple(Screen.Home, "Home", Icons.Default.Home),
                Triple(Screen.Chat, "NyayaAI", Icons.Default.ChatBubbleOutline),
                Triple(Screen.Documents, "Lawyers", Icons.Default.PersonSearch),
                Triple(Screen.Profile, "Profile", Icons.Default.PersonOutline)
            )
        }

        items.forEach { (screen, label, icon) ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                selected = isSelected,
// ... rest of the code
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) BrandIndigo else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Text(
                        text = label,
                        color = if (isSelected) BrandIndigo else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = BrandIndigo.copy(alpha = 0.1f)
                )
            )
        }
    }
}