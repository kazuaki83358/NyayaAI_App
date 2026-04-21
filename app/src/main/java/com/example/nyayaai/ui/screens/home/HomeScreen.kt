package com.example.nyayaai.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nyayaai.navigation.Screen
import com.example.nyayaai.ui.components.BottomNavBar
import com.example.nyayaai.ui.components.FloatingAiButton
import com.example.nyayaai.ui.components.GradientHeader
import com.example.nyayaai.ui.components.LawCategoryCard
import com.example.nyayaai.ui.theme.*

@Composable
fun HomeScreen(navController: NavController) {
    val brandBlue = Color(0xFF4F46E5)
    val darkText = MaterialTheme.colorScheme.onBackground
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    
    Scaffold(
        floatingActionButton = {
            FloatingAiButton(onClick = { navController.navigate(Screen.Chat.route) })
        },
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            item {
                GradientHeader(
                    title = "NyayaAI",
                    subtitle = "Premium legal intelligence at your fingertips"
                )
            }

            // 1. Legal Categories
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)
                ) {
                    Text(
                        text = "Legal Categories",
                        color = darkText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            LawCategoryCard(
                                title = "Family Law",
                                subtitle = "Matters of the heart and home",
                                color = FamilyRose,
                                icon = Icons.Default.Favorite
                            ) {
                                navController.navigate(Screen.Chat.route)
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            LawCategoryCard(
                                title = "Labour Law",
                                subtitle = "Professional rights & equity",
                                color = LabourBlue,
                                icon = Icons.Default.Work
                            ) {
                                navController.navigate(Screen.Chat.route)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            LawCategoryCard(
                                title = "Property",
                                subtitle = "Estate, land & acquisitions",
                                color = PropertyAmber,
                                icon = Icons.Default.HomeWork
                            ) {
                                navController.navigate(Screen.Chat.route)
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            LawCategoryCard(
                                title = "Criminal",
                                subtitle = "Justice and legal defense",
                                color = TrafficEmerald,
                                icon = Icons.Default.Gavel
                            ) {
                                navController.navigate(Screen.Chat.route)
                            }
                        }
                    }
                }
            }

            // 2. Top Lawyers
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Top Lawyers",
                            color = darkText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "View All",
                            color = brandBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { navController.navigate(Screen.Documents.route) }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(listOf("Adv. Ramesh Singh", "Adv. Priya Sharma", "Adv. Anil Mehta", "Adv. Sneha Patil")) { name ->
                            HomeLawyerCard(name, darkText, secondaryText) {
                                navController.navigate(Screen.Documents.route)
                            }
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun HomeLawyerCard(name: String, darkText: Color, secondaryText: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = secondaryText
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = darkText,
                maxLines = 1
            )
            Text(
                text = "Supreme Court",
                fontSize = 12.sp,
                color = secondaryText,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                    Text(" 4.9", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
