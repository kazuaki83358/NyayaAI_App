package com.example.nyayaai.ui.screens.choose

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChooseRoleScreen(
    onUserClick: () -> Unit,
    onLawyerClick: () -> Unit
) {
    val backgroundCream = Color(0xFFFDF8F3)
    val brandOrange = Color(0xFFD97706)
    val brandBlue = Color(0xFF4F46E5)

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val handleRoleSelection = { role: String, onSuccess: () -> Unit ->
        val uid = auth.currentUser?.uid
        if (uid != null) {
            isLoading = true
            errorMessage = null
            firestore.collection("users").document(uid)
                .update("role", role)
                .addOnSuccessListener {
                    isLoading = false
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    isLoading = false
                    errorMessage = "Failed to update role: ${e.localizedMessage}"
                }
        } else {
            // Fallback: if user session is somehow missing, still try to proceed
            onSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundCream)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // --- TOP LOGO ---
        Surface(
            modifier = Modifier.size(64.dp),
            color = brandOrange,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp
        ) {
            Icon(
                imageVector = Icons.Default.Gavel,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- WELCOME TEXT ---
        Text(
            text = "Welcome to NyayaAI",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = brandOrange,
            fontFamily = FontFamily.Serif
        )
        Text(
            text = "Choose how you'd like to continue",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage!!,
                color = Color.Red,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = brandOrange)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- USER CARD ---
        RoleSelectionCard(
            title = "Need Legal Help",
            description = "Get instant AI legal guidance, find lawyers, and manage your cases",
            features = listOf("AI Legal Assistant", "Find Expert Lawyers", "Emergency Legal Help", "Save & Track Cases"),
            buttonText = "Continue as User",
            accentColor = brandOrange,
            icon = Icons.Default.Chat,
            onClick = {
                if (!isLoading) {
                    handleRoleSelection("common_man", onUserClick)
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- LAWYER CARD ---
        RoleSelectionCard(
            title = "I am a Lawyer",
            description = "Manage clients, consultations, and grow your legal practice",
            features = listOf("Client Requests", "Video & Call Consultations", "Chat with Clients", "Track Earnings"),
            buttonText = "Continue as Lawyer",
            accentColor = brandBlue,
            icon = Icons.Default.Work,
            onClick = {
                if (!isLoading) {
                    handleRoleSelection("lawyer", onLawyerClick)
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- PROTOTYPE INFO BOX ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
        ) {
            Text(
                text = "Test Prototype: Choose 'Need Legal Help' or 'I am a Lawyer' to finalize registration.",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun RoleSelectionCard(
    title: String,
    description: String,
    features: List<String>,
    buttonText: String,
    accentColor: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(15.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Icon Square
            Surface(
                modifier = Modifier.size(56.dp),
                color = accentColor,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Bullet Points
            features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = feature, fontSize = 14.sp, color = Color(0xFF4B5563))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Text
            Row(
                modifier = Modifier.clickable { onClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$buttonText  →",
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
