package com.example.nyayaai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.nyayaai.ui.theme.BrandIndigo

@Composable
fun ProfileCard() {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    var userName by remember { mutableStateOf("User") }
    var userEmail by remember { mutableStateOf("Secured Legal Identity") }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        userName = doc.getString("fullName") ?: "User"
                        userEmail = doc.getString("email") ?: "Secured Legal Identity"
                    }
                }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = BrandIndigo.copy(alpha = 0.12f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(14.dp),
                    tint = BrandIndigo
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column {
                Text(
                    text = userName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-0.5).sp
                )
                
                Text(
                    text = userEmail,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}