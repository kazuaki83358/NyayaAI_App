package com.example.nyayaai.ui.screens.lawerdashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nyayaai.navigation.Screen
import com.example.nyayaai.ui.components.BottomNavBar

@Composable
fun LawyerDashboardScreen(navController: NavController) {
    val brandOrange = Color(0xFFD97706)
    val brandBlue = Color(0xFF4F46E5)
    val darkText = MaterialTheme.colorScheme.onBackground
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Cool Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        brush = Brush.verticalGradient(listOf(brandBlue, brandBlue.copy(alpha = 0.8f))),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Welcome back,", color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
                            Text("Adv. Ramesh Singh", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.padding(12.dp))
                        }
                    }
                }
            }

            // Stats Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-30).dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardStatCard("Total Earnings", "₹45,200", Icons.Default.Payments, brandOrange, darkText, secondaryText, Modifier.weight(1f))
                DashboardStatCard("Consultations", "128", Icons.Default.EventAvailable, brandBlue, darkText, secondaryText, Modifier.weight(1f))
            }

            // Quick Actions
            Text(
                text = "Quick Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = darkText,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            LazyRow(
                modifier = Modifier.padding(vertical = 16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { QuickActionItem("New Case", Icons.Default.Add, brandOrange, darkText) }
                item { QuickActionItem("Schedule", Icons.Default.CalendarToday, brandBlue, darkText) }
                item { QuickActionItem("Clients", Icons.Default.People, Color(0xFF10B981), darkText) }
                item { QuickActionItem("Documents", Icons.Default.Folder, MaterialTheme.colorScheme.onSurfaceVariant, darkText) }
            }

            // Active Requests
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Client Requests", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkText)
                Text(
                    "View All",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { navController.navigate(Screen.LawyerRequests.route) }
                )
            }

            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                listOf(
                    RequestData("Sarah Khan", "Family Law", "Urgent", "15 mins ago"),
                    RequestData("Amit Patel", "Property Dispute", "Pending", "2 hours ago"),
                    RequestData("Vijay Verma", "Criminal Defense", "Pending", "5 hours ago")
                ).forEach { request ->
                    DashboardRequestCard(request, darkText, secondaryText)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DashboardStatCard(label: String, value: String, icon: ImageVector, color: Color, darkText: Color, secondaryText: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.shadow(12.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = label, fontSize = 12.sp, color = secondaryText, fontWeight = FontWeight.Medium)
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = darkText)
        }
    }
}

@Composable
fun QuickActionItem(label: String, icon: ImageVector, color: Color, darkText: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(16.dp),
            color = color.copy(alpha = 0.15f)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(18.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = darkText)
    }
}

@Composable
fun DashboardRequestCard(request: RequestData, darkText: Color, secondaryText: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(request.name.first().toString(), fontWeight = FontWeight.ExtraBold, color = darkText)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(request.name, fontWeight = FontWeight.Bold, color = darkText, fontSize = 16.sp)
                Text(request.type, fontSize = 13.sp, color = secondaryText, fontWeight = FontWeight.Medium)
            }
            Column(horizontalAlignment = Alignment.End) {
                val statusColor = if (request.status == "Urgent") Color(0xFFDC2626) else Color(0xFFD97706)
                Text(request.status, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                Text(request.time, fontSize = 11.sp, color = secondaryText, fontWeight = FontWeight.Medium)
            }
        }
    }
}

data class RequestData(val name: String, val type: String, val status: String, val time: String)
