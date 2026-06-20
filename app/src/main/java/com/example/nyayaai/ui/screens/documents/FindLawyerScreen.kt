package com.example.nyayaai.ui.screens.documents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
    val uid: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindLawyerScreen(navController: NavController) {
    val darkText = Color(0xFF0F172A)
    val secondaryText = Color(0xFF475569)

    val firestore = FirebaseFirestore.getInstance()
    var lawyers by remember { mutableStateOf<List<Lawyer>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

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
                subtitle = "Consult with expert legal professionals"
            )

            // Search Bar
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

            // Filters
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

            // Lawyer List
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4F46E5))
                }
            } else {
                val auth = FirebaseAuth.getInstance()
                val scope = rememberCoroutineScope()
                var requestMessage by remember { mutableStateOf<String?>(null) }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        if (requestMessage != null) {
                            Text(
                                requestMessage!!,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(lawyers) { lawyer ->
                        LawyerCard(lawyer, darkText, secondaryText, onReqClick = {
                            val currentUser = auth.currentUser
                            if (currentUser != null) {
                                scope.launch {
                                    try {
                                        val request = hashMapOf(
                                            "lawyerId" to lawyer.uid,
                                            "lawyerName" to lawyer.name,
                                            "userId" to currentUser.uid,
                                            "userName" to (currentUser.email?.substringBefore("@") ?: "User"),
                                            "status" to "pending",
                                            "timestamp" to com.google.firebase.Timestamp.now(),
                                            "type" to lawyer.specialization
                                        )
                                        firestore.collection("requests").add(request).await()
                                        requestMessage = "Request sent to ${lawyer.name}"
                                    } catch (e: Exception) {
                                        requestMessage = "Error: ${e.localizedMessage}"
                                    }
                                }
                            }
                        }) {
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
fun LawyerCard(lawyer: Lawyer, darkText: Color, secondaryText: Color, onReqClick: () -> Unit, onClick: () -> Unit) {
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
                // Profile Image Placeholder
                Surface(
                    modifier = Modifier.size(64.dp),
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Star, null, tint = Color.LightGray, modifier = Modifier.padding(16.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lawyer.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText
                    )
                    Text(
                        text = "${lawyer.specialization} • ${lawyer.city}",
                        fontSize = 13.sp,
                        color = secondaryText,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = " ${lawyer.rating}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFB45309)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = lawyer.price,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFD97706)
                    )
                    Text(
                        text = "per session",
                        fontSize = 10.sp,
                        color = secondaryText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = onReqClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
            ) {
                Text("Request Consultation", fontWeight = FontWeight.Bold)
            }
        }
    }
}
