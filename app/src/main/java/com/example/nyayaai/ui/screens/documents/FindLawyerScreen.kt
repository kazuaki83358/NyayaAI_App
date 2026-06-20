package com.example.nyayaai.ui.screens.documents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nyayaai.navigation.Screen
import com.example.nyayaai.ui.components.BottomNavBar
import com.example.nyayaai.ui.components.GradientHeader
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Lawyer(
    val name: String,
    val specialization: String,
    val city: String,
    val language: String,
    val price: String,
    val rating: Double,
    val uid: String = "",
    val requestStatus: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindLawyerScreen(navController: NavController) {
    val darkText = Color(0xFF0F172A)
    val secondaryText = Color(0xFF475569)

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val currentUserId = auth.currentUser?.uid
    
    var lawyers by remember { mutableStateOf<List<Lawyer>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var userRequests by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var requestToId by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    DisposableEffect(currentUserId) {
        if (currentUserId == null) return@DisposableEffect onDispose {}

        val listener = firestore.collection("requests")
            .whereEqualTo("userId", currentUserId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val reqMap = mutableMapOf<String, String>()
                    val idMap = mutableMapOf<String, String>()
                    snapshot.documents.forEach { doc ->
                        val lawyerId = doc.getString("lawyerId") ?: ""
                        val status = doc.getString("status") ?: ""
                        reqMap[lawyerId] = status
                        idMap[lawyerId] = doc.id
                    }
                    userRequests = reqMap
                    requestToId = idMap
                }
            }
        
        onDispose { listener.remove() }
    }

    LaunchedEffect(Unit) {
        firestore.collection("users")
            .whereEqualTo("role", "lawyer")
            .get()
            .addOnSuccessListener { result ->
                val lawyerList = result.documents.map { doc ->
                    Lawyer(
                        name = doc.getString("fullName") ?: "Unknown Lawyer",
                        specialization = doc.getString("specialization") ?: "General Practice",
                        city = doc.getString("city") ?: "Unknown City",
                        language = doc.getString("languages") ?: "English",
                        price = "₹${doc.getString("fee") ?: "0"}",
                        rating = doc.getDouble("rating") ?: 4.5,
                        uid = doc.id
                    )
                }
                lawyers = lawyerList
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    val filters = listOf("City", "Specialization", "Language", "Price")
    var selectedFilter by remember { mutableStateOf("All") }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFDF8F6))
        ) {
            GradientHeader(
                title = "Find a Lawyer",
                subtitle = "Consult with expert legal professionals",
                onNotificationClick = { navController.navigate(Screen.Notifications.route) }
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search lawyers, cities, or specialization", color = secondaryText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = secondaryText) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = darkText,
                    unfocusedTextColor = darkText,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF4F46E5),
                            selectedLabelColor = Color.White,
                            labelColor = darkText
                        )
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4F46E5))
                }
            } else {
                val scope = rememberCoroutineScope()
                var statusMsg by remember { mutableStateOf<String?>(null) }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        if (statusMsg != null) {
                            Text(
                                statusMsg!!,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(lawyers) { lawyer ->
                        val status = userRequests[lawyer.uid] ?: ""
                        val requestId = requestToId[lawyer.uid] ?: ""

                        LawyerCard(
                            lawyer = lawyer,
                            darkText = darkText,
                            secondaryText = secondaryText,
                            status = status,
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
                                            statusMsg = "Request sent to ${lawyer.name}"
                                        } catch (e: Exception) {
                                            statusMsg = "Error: ${e.localizedMessage}"
                                        }
                                    }
                                }
                            },
                            onChatClick = {
                                navController.navigate(Screen.ChatDetail.createRoute(requestId))
                            }
                        ) {
                            // Navigate to details
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
fun LawyerCard(
    lawyer: Lawyer, 
    darkText: Color, 
    secondaryText: Color, 
    status: String,
    onReqClick: () -> Unit, 
    onChatClick: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Star, null, tint = Color.LightGray, modifier = Modifier.padding(16.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(lawyer.name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = darkText)
                    Text("${lawyer.specialization} • ${lawyer.city}", fontSize = 13.sp, color = secondaryText, fontWeight = FontWeight.Medium)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                        Text(" ${lawyer.rating}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFB45309))
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(lawyer.price, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD97706))
                    Text("per session", fontSize = 10.sp, color = secondaryText, fontWeight = FontWeight.Bold)
                }
            }

            if (status == "accepted") {
                Button(
                    onClick = onChatClick,
                    modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, bottom = 12.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Icon(Icons.Default.Chat, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chat Now", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onReqClick,
                    enabled = status == "",
                    modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, bottom = 12.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F46E5),
                        disabledContainerColor = when(status) {
                            "pending" -> Color(0xFFEF4444)
                            "rejected" -> Color(0xFFEF4444)
                            else -> Color(0xFFE2E8F0)
                        },
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(
                        text = when(status) {
                            "pending" -> "Requested Pending"
                            "rejected" -> "Request Rejected"
                            else -> "Request Consultation"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
