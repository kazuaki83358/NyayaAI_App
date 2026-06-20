package com.example.nyayaai.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nyayaai.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val status: String,
    val requestId: String,
    val type: String = "request" // "request" or "chat"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("requests")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        val list = snapshot.documents.filter { doc ->
                            doc.getString("userId") == uid || doc.getString("lawyerId") == uid
                        }.map { doc ->
                            val status = doc.getString("status") ?: "pending"
                            val lawyerName = doc.getString("lawyerName") ?: "Lawyer"
                            val userName = doc.getString("userName") ?: "Client"
                            val isLawyer = doc.getString("lawyerId") == uid
                            
                            val title = if (isLawyer) {
                                when (status) {
                                    "pending" -> "New Request from $userName"
                                    "accepted" -> "Consultation with $userName"
                                    else -> "Request from $userName"
                                }
                            } else {
                                when (status) {
                                    "accepted" -> "Request Accepted!"
                                    "rejected" -> "Request Declined"
                                    else -> "Request Pending"
                                }
                            }
                            
                            val message = if (isLawyer) {
                                when (status) {
                                    "pending" -> "$userName is seeking legal help. View details to accept."
                                    "accepted" -> "You have accepted the request. You can now chat."
                                    else -> "The request is $status"
                                }
                            } else {
                                when (status) {
                                    "accepted" -> "$lawyerName has accepted your request. Chat is now active."
                                    "rejected" -> "Unfortunately, $lawyerName cannot take your request."
                                    else -> "Waiting for $lawyerName to respond."
                                }
                            }

                            Notification(
                                id = doc.id,
                                title = title,
                                message = message,
                                time = "Real-time",
                                status = status,
                                requestId = doc.id
                            )
                        }
                        notifications = list
                    }
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No updates yet", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notifications) { notification ->
                        NotificationCard(notification) {
                            if (notification.status == "accepted") {
                                navController.navigate(Screen.ChatDetail.createRoute(notification.requestId))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: Notification, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = if (notification.type == "chat") Icons.Default.Chat else Icons.Default.Notifications
            val tint = if (notification.type == "chat") Color(0xFF4F46E5) else when (notification.status) {
                "accepted" -> Color(0xFF10B981)
                "rejected" -> Color(0xFFEF4444)
                else -> MaterialTheme.colorScheme.primary
            }

            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = tint.copy(alpha = 0.1f)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(notification.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(notification.message, fontSize = 13.sp, color = Color.Gray)
                Text(notification.time, fontSize = 11.sp, color = Color.LightGray, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}
