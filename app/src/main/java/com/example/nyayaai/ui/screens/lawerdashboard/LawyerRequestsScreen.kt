package com.example.nyayaai.ui.screens.lawerdashboard

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
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
import androidx.navigation.NavController
import com.example.nyayaai.navigation.Screen
import com.example.nyayaai.ui.components.BottomNavBar
import com.example.nyayaai.ui.components.GradientHeader
import com.example.nyayaai.ui.screens.home.sendLocalNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class RequestItem(
    val id: String,
    val name: String,
    val type: String,
    val status: String,
    val userId: String
)

@Composable
fun LawyerRequestsScreen(navController: NavController) {
    val context = LocalContext.current
    val darkText = MaterialTheme.colorScheme.onBackground
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()
    
    var requests by remember { mutableStateOf<List<RequestItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val currentUid = auth.currentUser?.uid
    DisposableEffect(currentUid) {
        if (currentUid == null) {
            isLoading = false
            return@DisposableEffect onDispose {}
        }

        Log.d("LAWYER_REQ", "Setting up snapshot listener for UID: $currentUid")
        val listener = firestore.collection("requests")
            .whereEqualTo("lawyerId", currentUid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("LAWYER_REQ", "Listen failed", e)
                    isLoading = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d("LAWYER_REQ", "Snapshot received with ${snapshot.size()} docs. Changes: ${snapshot.documentChanges.size}")
                    
                    snapshot.documentChanges.forEach { diff ->
                        if (diff.type == DocumentChange.Type.ADDED) {
                            val status = diff.document.getString("status") ?: "pending"
                            if (status == "pending") {
                                val userName = diff.document.getString("userName") ?: "New Client"
                                sendLocalNotification(context, "New Legal Request", "$userName is seeking your consultation.")
                            }
                        }
                    }

                    requests = snapshot.documents.map { doc ->
                        RequestItem(
                            id = doc.id,
                            name = doc.getString("userName") ?: "Unknown User",
                            type = doc.getString("type") ?: "Legal Consultation",
                            status = doc.getString("status") ?: "pending",
                            userId = doc.getString("userId") ?: ""
                        )
                    }
                }
                isLoading = false
            }

        onDispose {
            Log.d("LAWYER_REQ", "Disposing listener")
            listener.remove()
        }
    }

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

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (requests.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No requests yet", color = secondaryText)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(requests, key = { it.id }) { request ->
                        RequestItemCard(request, darkText, secondaryText, onAccept = {
                            scope.launch {
                                try {
                                    firestore.collection("requests").document(request.id)
                                        .update("status", "accepted")
                                        .await()
                                    Log.d("LAWYER_REQ", "Accepted and navigating to chat")
                                    navController.navigate(Screen.ChatDetail.createRoute(request.id))
                                } catch (e: Exception) {
                                    Log.e("LAWYER_REQ", "Failed to accept", e)
                                }
                            }
                        }, onReject = {
                            scope.launch {
                                try {
                                    firestore.collection("requests").document(request.id)
                                        .update("status", "rejected")
                                        .await()
                                    Log.d("LAWYER_REQ", "Rejected ${request.id}")
                                } catch (e: Exception) {
                                    Log.e("LAWYER_REQ", "Failed to reject", e)
                                }
                            }
                        }, onChat = {
                            navController.navigate(Screen.ChatDetail.createRoute(request.id))
                        })
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
fun RequestItemCard(
    request: RequestItem, 
    darkText: Color, 
    secondaryText: Color,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onChat: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = darkText)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(request.name, fontWeight = FontWeight.Bold, color = darkText, fontSize = 16.sp)
                    Text(request.type, fontSize = 13.sp, color = secondaryText, fontWeight = FontWeight.Medium)
                }
                Text(
                    request.status.replaceFirstChar { it.uppercase() }, 
                    color = when(request.status) {
                        "accepted" -> Color(0xFF10B981)
                        "rejected" -> Color(0xFFEF4444)
                        else -> Color(0xFFD97706)
                    }, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.ExtraBold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (request.status == "pending") {
                    IconButton(onClick = onReject, colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFFEE2E2))) {
                        Icon(Icons.Default.Close, contentDescription = "Reject", tint = Color(0xFFEF4444))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onAccept, colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFD1FAE5))) {
                        Icon(Icons.Default.Check, contentDescription = "Accept", tint = Color(0xFF10B981))
                    }
                } else if (request.status == "accepted") {
                    Button(
                        onClick = onChat,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chat Now")
                    }
                }
            }
        }
    }
}
