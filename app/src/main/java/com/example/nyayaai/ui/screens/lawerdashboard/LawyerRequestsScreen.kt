package com.example.nyayaai.ui.screens.lawerdashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nyayaai.ui.components.BottomNavBar
import com.example.nyayaai.ui.components.GradientHeader

@Composable
fun LawyerRequestsScreen(navController: NavController) {
    val darkText = MaterialTheme.colorScheme.onBackground
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant

    val requests = listOf(
        RequestData("Sarah Khan", "Family Law", "Urgent", "15 mins ago"),
        RequestData("Amit Patel", "Property Dispute", "Pending", "2 hours ago"),
        RequestData("Vijay Verma", "Criminal Defense", "Pending", "5 hours ago"),
        RequestData("John Doe", "Contract Review", "Completed", "Yesterday"),
        RequestData("Rajesh Kumar", "Civil Litigation", "Pending", "1 day ago"),
        RequestData("Priya Singh", "Divorce Case", "Urgent", "2 days ago"),
        RequestData("Anil Mehta", "Corporate Law", "Pending", "3 days ago")
    )

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            GradientHeader(
                title = "All Requests",
                subtitle = "Manage all incoming client consultations"
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(requests) { request ->
                    DashboardRequestCard(request, darkText, secondaryText)
                }
            }
        }
    }
}
