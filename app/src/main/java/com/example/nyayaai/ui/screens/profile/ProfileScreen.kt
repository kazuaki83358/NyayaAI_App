package com.example.nyayaai.ui.screens.profile

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nyayaai.ui.components.BottomNavBar
import com.example.nyayaai.ui.components.ProfileCard
import com.example.nyayaai.ui.theme.*

@Composable
fun ProfileScreen(navController: NavController) {
    val themeManager = LocalThemeManager.current
    
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
                Box(modifier = Modifier.offset(y = (-40).dp)) {
                    ProfileCard()
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-20).dp)) {
                    Text(
                        text = "Emergency Contacts",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    EmergencyGrid()
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        text = "Preferences",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    SettingsRow(icon = Icons.Outlined.DarkMode, title = "Dark Mode") {
                        Switch(
                            checked = themeManager.isDark.value,
                            onCheckedChange = { themeManager.toggleTheme() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BrandIndigo,
                                checkedTrackColor = BrandIndigo.copy(alpha = 0.5f)
                            )
                        )
                    }
                    
                    SettingsRow(icon = Icons.Outlined.Notifications, title = "Notifications") {
                        Icon(Icons.Outlined.ChevronRight, null, tint = Color.Gray)
                    }
                    
                    SettingsRow(icon = Icons.Outlined.SupportAgent, title = "Help & Support") {
                        Icon(Icons.Outlined.ChevronRight, null, tint = Color.Gray)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
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
                text = "Profile",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Manage your legal identity",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EmergencyGrid() {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            EmergencyCard(label = "Police", number = "100", modifier = Modifier.weight(1f))
            EmergencyCard(label = "Women", number = "1091", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            EmergencyCard(label = "Ambulance", number = "102", modifier = Modifier.weight(1f))
            EmergencyCard(label = "Child Help", number = "1098", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun EmergencyCard(label: String, number: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Phone, null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = label, fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            }
            Text(text = number, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun SettingsRow(icon: ImageVector, title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(18.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = BrandIndigo, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = title, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
            }
            content()
        }
    }
}