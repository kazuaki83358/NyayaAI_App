package com.example.nyayaai.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)
                ) {
                    Text(
                        text = "Legal Categories",
                        color = MaterialTheme.colorScheme.onBackground,
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
        }
    }
}