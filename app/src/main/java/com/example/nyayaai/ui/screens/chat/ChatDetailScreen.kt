package com.example.nyayaai.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nyayaai.ui.screens.home.sendLocalNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentChange

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(navController: NavController, requestId: String) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""
    
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var newMessage by remember { mutableStateOf("") }
    var chatPartnerName by remember { mutableStateOf("Chat") }

    LaunchedEffect(requestId) {
        // Get chat partner name
        firestore.collection("requests").document(requestId).get()
            .addOnSuccessListener { doc ->
                val lawyerId = doc.getString("lawyerId")
                chatPartnerName = if (currentUserId == lawyerId) {
                    doc.getString("userName") ?: "Client"
                } else {
                    doc.getString("lawyerName") ?: "Lawyer"
                }
            }

        // Listen for messages
        firestore.collection("requests").document(requestId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    // Send notification for new incoming messages
                    snapshot.documentChanges.forEach { diff ->
                        if (diff.type == DocumentChange.Type.ADDED) {
                            val msg = diff.document.toObject(ChatMessage::class.java)
                            if (msg.senderId != "" && msg.senderId != currentUserId) {
                                sendLocalNotification(context, "New Message", msg.message)
                            }
                        }
                    }

                    messages = snapshot.documents.map { doc ->
                        ChatMessage(
                            id = doc.id,
                            senderId = doc.getString("senderId") ?: "",
                            message = doc.getString("message") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0L
                        )
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(chatPartnerName, fontWeight = FontWeight.Bold) },
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
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    ChatBubble(msg, msg.senderId == currentUserId)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newMessage,
                    onValueChange = { newMessage = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") },
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = {
                        if (newMessage.isNotBlank()) {
                            val msgMap = hashMapOf(
                                "senderId" to currentUserId,
                                "message" to newMessage,
                                "timestamp" to System.currentTimeMillis()
                            )
                            firestore.collection("requests").document(requestId)
                                .collection("messages").add(msgMap)
                            newMessage = ""
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, isCurrentUser: Boolean) {
    val bubbleColor = if (isCurrentUser) MaterialTheme.colorScheme.primary else Color(0xFFF1F5F9)
    val textColor = if (isCurrentUser) Color.White else Color.Black
    val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isCurrentUser) 16.dp else 0.dp,
                bottomEnd = if (isCurrentUser) 0.dp else 16.dp
            )
        ) {
            Text(
                text = message.message,
                color = textColor,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                fontSize = 15.sp
            )
        }
    }
}
