package com.example.nyayaai.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.nyayaai.data.PreferenceManager
import com.example.nyayaai.ui.components.BottomNavBar
import com.example.nyayaai.ui.components.GradientHeader
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val preferenceManager = PreferenceManager(context)
    val scope = rememberCoroutineScope()

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    var isLoading by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val primaryOrange = Color(0xFFD97706)
    val darkBlueText = Color(0xFF1E293B)

    var name by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var languages by remember { mutableStateOf("") }
    var fee by remember { mutableStateOf("") }
    var barNumber by remember { mutableStateOf("") }
    var about by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        name = doc.getString("fullName") ?: ""
                        experience = doc.getString("experience") ?: ""
                        specialization = doc.getString("specialization") ?: ""
                        city = doc.getString("city") ?: ""
                        languages = doc.getString("languages") ?: ""
                        fee = doc.getString("fee") ?: ""
                        barNumber = doc.getString("barNumber") ?: ""
                        about = doc.getString("about") ?: ""
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Professional Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryOrange)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFFDF8F6))
                    .verticalScroll(rememberScrollState())
            ) {
                GradientHeader(
                    title = "Expertise Details",
                    subtitle = "Share your professional background with potential clients"
                )

                Surface(
                    modifier = Modifier
                        .padding(24.dp)
                        .shadow(16.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Basic Information", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = darkBlueText)
                        Spacer(modifier = Modifier.height(20.dp))

                        ProfileInputField("Full Name", name, darkBlueText) { name = it }
                        ProfileInputField("Years of Experience", experience, darkBlueText) { experience = it }
                        ProfileInputField("Practice Area (e.g., Criminal, Family)", specialization, darkBlueText) { specialization = it }
                        ProfileInputField("Current City", city, darkBlueText) { city = it }
                        ProfileInputField("Languages Known", languages, darkBlueText) { languages = it }
                        ProfileInputField("Consultation Fee (₹)", fee, darkBlueText) { fee = it }
                        ProfileInputField("Bar Council Enrollment ID", barNumber, darkBlueText) { barNumber = it }
                        ProfileInputField("Professional Summary", about, darkBlueText, singleLine = false) { about = it }

                        if (errorMessage != null) {
                            Text(errorMessage!!, color = Color.Red, fontSize = 13.sp, modifier = Modifier.padding(bottom = 12.dp))
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                val uid = auth.currentUser?.uid
                                if (uid == null) {
                                    errorMessage = "User not logged in"
                                    return@Button
                                }

                                isUpdating = true
                                errorMessage = null
                                
                                scope.launch {
                                    try {
                                        val lawyerMap = hashMapOf(
                                            "fullName" to name,
                                            "experience" to experience,
                                            "specialization" to specialization,
                                            "city" to city,
                                            "languages" to languages,
                                            "fee" to fee,
                                            "barNumber" to barNumber,
                                            "about" to about,
                                            "role" to "lawyer",
                                            "rating" to 4.5
                                        )
                                        
                                        firestore.collection("users").document(uid).update(lawyerMap as Map<String, Any>).await()
                                        
                                        preferenceManager.saveLawyerProfile(
                                            name, experience, specialization, city, languages, fee, barNumber, about
                                        )
                                        
                                        isUpdating = false
                                        navController.navigateUp()
                                    } catch (e: Exception) {
                                        isUpdating = false
                                        errorMessage = "Update failed: ${e.localizedMessage}"
                                    }
                                }
                            },
                            enabled = !isUpdating,
                            modifier = Modifier.fillMaxWidth().height(56.dp).shadow(12.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryOrange)
                        ) {
                            if (isUpdating) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Update Information", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInputField(label: String, value: String, textColor: Color, singleLine: Boolean = true, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            minLines = if (singleLine) 1 else 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedBorderColor = Color(0xFFD97706),
                focusedContainerColor = Color(0xFFF8FAFC),
                unfocusedContainerColor = Color(0xFFF8FAFC)
            )
        )
    }
}
