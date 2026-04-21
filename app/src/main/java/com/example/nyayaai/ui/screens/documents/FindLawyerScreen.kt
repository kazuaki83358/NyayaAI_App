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

data class Lawyer(
    val name: String,
    val specialization: String,
    val city: String,
    val language: String,
    val price: String,
    val rating: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindLawyerScreen(navController: NavController) {
    val darkText = Color(0xFF0F172A)
    val secondaryText = Color(0xFF475569)

    val lawyers = listOf(
        Lawyer("Adv. Ramesh Singh", "Criminal Law", "Mumbai", "Hindi, English", "₹2000", 4.8),
        Lawyer("Adv. Priya Sharma", "Family Law", "Delhi", "English, Hindi", "₹1500", 4.9),
        Lawyer("Adv. Anil Mehta", "Corporate Law", "Bangalore", "English, Kannada", "₹5000", 4.7),
        Lawyer("Adv. Sneha Patil", "Civil Law", "Pune", "Marathi, Hindi", "₹1200", 4.6),
        Lawyer("Adv. Vikram Goel", "Tax Law", "Gurgaon", "English, Hindi", "₹3500", 4.5)
    )

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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                items(lawyers) { lawyer ->
                    LawyerCard(lawyer, darkText, secondaryText) {
                        // Navigate to details
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun LawyerCard(lawyer: Lawyer, darkText: Color, secondaryText: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
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
                    Text(
                        text = " (120+ reviews)",
                        fontSize = 11.sp,
                        color = secondaryText,
                        fontWeight = FontWeight.Medium
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
    }
}
