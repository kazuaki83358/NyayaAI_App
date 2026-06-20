package com.example.nyayaai.ui.screens.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.nyayaai.navigation.Screen
import com.example.nyayaai.ui.components.BottomNavBar
import com.example.nyayaai.ui.components.FloatingAiButton
import com.example.nyayaai.ui.components.GradientHeader
import com.example.nyayaai.ui.components.LawCategoryCard
import com.example.nyayaai.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class HomeLawyer(
    val uid: String,
    val name: String,
    val specialization: String
)

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val brandBlue = Color(0xFF4F46E5)
    val darkText = MaterialTheme.colorScheme.onBackground
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val currentUserId = auth.currentUser?.uid
    
    var topLawyers by remember { mutableStateOf<List<HomeLawyer>>(emptyList()) }
    var userRequests by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var requestToId by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM_TOKEN", "Token: ${task.result}")
            }
        }

        if (currentUserId != null) {
            firestore.collection("requests")
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        val reqMap = mutableMapOf<String, String>()
                        val idMap = mutableMapOf<String, String>()
                        snapshot.documents.forEach { doc ->
                            val lawyerId = doc.getString("lawyerId") ?: ""
                            val status = doc.getString("status") ?: ""
                            val oldStatus = userRequests[lawyerId]
                            
                            if (oldStatus == "pending" && status == "accepted") {
                                sendLocalNotification(context, "Request Accepted!", "${doc.getString("lawyerName")} has accepted your request.")
                            }
                            
                            reqMap[lawyerId] = status
                            idMap[lawyerId] = doc.id
                        }
                        userRequests = reqMap
                        requestToId = idMap
                    }
                }
        }

        firestore.collection("users")
            .whereEqualTo("role", "lawyer")
            .limit(5)
            .get()
            .addOnSuccessListener { result ->
                topLawyers = result.documents.map { doc ->
                    HomeLawyer(
                        uid = doc.id,
                        name = doc.getString("fullName") ?: "Lawyer",
                        specialization = doc.getString("specialization") ?: "Legal Expert"
                    )
                }
            }
    }
    
    Scaffold(
        floatingActionButton = {
            FloatingAiButton(onClick = { navController.navigate(Screen.Chat.route) })
        },
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            item {
                GradientHeader(
                    title = "NyayaAI",
                    subtitle = "Premium legal intelligence at your fingertips",
                    onNotificationClick = { navController.navigate(Screen.Notifications.route) }
                )
            }

            // 1. Legal Categories
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)
                ) {
                    Text(
                        text = "Legal Categories",
                        color = darkText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            LawCategoryCard(
                                title = "Family Law",
                                subtitle = "Matters of the heart and home",
                                color = FamilyRose,
                                icon = Icons.Default.Favorite
                            ) {
                                navController.navigate(Screen.Chat.route)
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            LawCategoryCard(
                                title = "Labour Law",
                                subtitle = "Professional rights & equity",
                                color = LabourBlue,
                                icon = Icons.Default.Work
                            ) {
                                navController.navigate(Screen.Chat.route)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            LawCategoryCard(
                                title = "Property",
                                subtitle = "Estate, land & acquisitions",
                                color = PropertyAmber,
                                icon = Icons.Default.HomeWork
                            ) {
                                navController.navigate(Screen.Chat.route)
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            LawCategoryCard(
                                title = "Criminal",
                                subtitle = "Justice and legal defense",
                                color = TrafficEmerald,
                                icon = Icons.Default.Gavel
                            ) {
                                navController.navigate(Screen.Chat.route)
                            }
                        }
                    }
                }
            }

            // 2. Top Lawyers
            item {
                val scope = rememberCoroutineScope()
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Top Lawyers",
                            color = darkText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "View All",
                            color = brandBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { 
                                navController.navigate(Screen.Documents.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(topLawyers) { lawyer ->
                            val status = userRequests[lawyer.uid] ?: ""
                            val requestId = requestToId[lawyer.uid] ?: ""

                            HomeLawyerCard(
                                name = lawyer.name,
                                specialization = lawyer.specialization,
                                status = status,
                                darkText = darkText,
                                secondaryText = secondaryText,
                                onReqClick = {
                                    if (currentUserId != null) {
                                        scope.launch {
                                            try {
                                                val request = hashMapOf(
                                                    "lawyerId" to lawyer.uid,
                                                    "lawyerName" to lawyer.name,
                                                    "userId" to currentUserId,
                                                    "userName" to (auth.currentUser?.email?.substringBefore("@") ?: "User"),
                                                    "status" to "pending",
                                                    "timestamp" to com.google.firebase.Timestamp.now(),
                                                    "type" to lawyer.specialization
                                                )
                                                firestore.collection("requests").add(request).await()
                                            } catch (e: Exception) {
                                                Log.e("HOME", "Request failed", e)
                                            }
                                        }
                                    }
                                },
                                onChatClick = {
                                    navController.navigate(Screen.ChatDetail.createRoute(requestId))
                                }
                            ) {
                                navController.navigate(Screen.Documents.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun HomeLawyerCard(
    name: String, 
    specialization: String,
    status: String,
    darkText: Color, 
    secondaryText: Color, 
    onReqClick: () -> Unit,
    onChatClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = secondaryText
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = darkText,
                maxLines = 1
            )
            Text(
                text = specialization,
                fontSize = 11.sp,
                color = secondaryText,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            if (status == "accepted") {
                Button(
                    onClick = onChatClick,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Icon(Icons.Default.Chat, null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onReqClick,
                    enabled = status == "",
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F46E5),
                        disabledContainerColor = if (status == "pending") Color(0xFFEF4444) else Color(0xFFE2E8F0),
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (status == "pending") "Pending" else "Request",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun sendLocalNotification(context: Context, title: String, message: String) {
    val channelId = "nyaya_notif_channel"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "NyayaAI Notifications", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(System.currentTimeMillis().toInt(), notification)
}
