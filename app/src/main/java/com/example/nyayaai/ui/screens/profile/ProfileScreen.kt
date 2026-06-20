package com.example.nyayaai.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nyayaai.data.PreferenceManager
import com.example.nyayaai.navigation.Screen
import com.example.nyayaai.ui.components.BottomNavBar
import com.example.nyayaai.ui.components.ProfileCard
import com.example.nyayaai.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController) {
    val themeManager = LocalThemeManager.current
    val context = LocalContext.current
    val preferenceManager = PreferenceManager(context)
    val userRole = preferenceManager.userRole.collectAsState(initial = "common_man")
    val scope = rememberCoroutineScope()
    
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    var userData by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        userData = doc.data ?: emptyMap()
                    }
                }
        }
    }

    val brandOrange = Color(0xFFD97706)
    val darkText = MaterialTheme.colorScheme.onBackground
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                ProfileHeader()
            }

            item {
                Box(
                    modifier = Modifier
                        .offset(y = (-40).dp)
                        .clickable(enabled = userRole.value == "lawyer") {
                            navController.navigate(Screen.LawyerProfile.route)
                        }
                ) {
                    ProfileCard()
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-20).dp)) {
                    if (userRole.value == "lawyer") {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .shadow(8.dp, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            color = brandOrange
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .clickable { navController.navigate(Screen.LawyerProfile.route) },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.AssignmentInd, null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text("Complete Profile", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                        Text("Experience, Fees, Bar ID", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                                    }
                                }
                                Icon(Icons.Outlined.ChevronRight, null, tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    } else if (userData.isNotEmpty()) {
                        Text(
                            text = "Account Details",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = darkText,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        DetailRow("Full Name", userData["fullName"]?.toString() ?: "N/A", darkText, secondaryText)
                        DetailRow("Email", userData["email"]?.toString() ?: "N/A", darkText, secondaryText)
                        DetailRow("Phone", userData["phone"]?.toString() ?: "N/A", darkText, secondaryText)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    Text(
                        text = "Emergency Contacts",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = darkText,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    EmergencyGrid(darkText, secondaryText)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        text = "App Preferences",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = darkText,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    SettingsRow(icon = Icons.Outlined.DarkMode, title = "Dark Mode", darkText = darkText) {
                        Switch(
                            checked = themeManager.isDark.value,
                            onCheckedChange = { themeManager.toggleTheme() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BrandIndigo,
                                checkedTrackColor = BrandIndigo.copy(alpha = 0.5f)
                            )
                        )
                    }
                    
                    SettingsRow(icon = Icons.Outlined.Notifications, title = "Notifications", darkText = darkText) {
                        IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                            Icon(Icons.Outlined.ChevronRight, null, tint = secondaryText)
                        }
                    }
                    
                    SettingsRow(icon = Icons.Outlined.Logout, title = "Logout", titleColor = Color.Red, darkText = darkText) {
                        IconButton(onClick = {
                            scope.launch {
                                preferenceManager.setLoggedIn(false)
                                preferenceManager.setUserRole("")
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }) {
                            Icon(Icons.Outlined.ChevronRight, null, tint = secondaryText)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, darkText: Color, secondaryText: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = secondaryText, fontSize = 14.sp)
        Text(value, color = darkText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun ProfileHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                brush = Brush.horizontalGradient(listOf(BrandIndigo, BrandPurple))
            )
            .padding(start = 24.dp, bottom = 60.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column {
            Text(
                text = "My Identity",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Manage your professional & personal legal data",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EmergencyGrid(darkText: Color, secondaryText: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            EmergencyCard(label = "Police", number = "100", darkText, secondaryText, modifier = Modifier.weight(1f))
            EmergencyCard(label = "Women", number = "1091", darkText, secondaryText, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            EmergencyCard(label = "Ambulance", number = "102", darkText, secondaryText, modifier = Modifier.weight(1f))
            EmergencyCard(label = "Child Help", number = "1098", darkText, secondaryText, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun EmergencyCard(label: String, number: String, darkText: Color, secondaryText: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Phone, null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = label, fontSize = 14.sp, color = secondaryText, fontWeight = FontWeight.Bold)
            }
            Text(text = number, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = darkText)
        }
    }
}

@Composable
fun SettingsRow(icon: ImageVector, title: String, titleColor: Color = Color.Unspecified, darkText: Color, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(18.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = if (titleColor == Color.Red) Color.Red else BrandIndigo, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = title, fontSize = 16.sp, color = if (titleColor == Color.Red) Color.Red else darkText, fontWeight = FontWeight.Bold)
            }
            content()
        }
    }
}
