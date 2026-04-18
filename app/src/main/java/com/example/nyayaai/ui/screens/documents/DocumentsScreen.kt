package com.example.nyayaai.ui.screens.documents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.nyayaai.ui.components.BottomNavBar
import com.example.nyayaai.ui.components.DocumentCard
import com.example.nyayaai.ui.components.GradientHeader

@Composable
fun DocumentsScreen(navController: NavController) {
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
                GradientHeader(
                    title = "Library",
                    subtitle = "Manage your legal documentation and drafts"
                )
            }

            items(6) {
                DocumentCard()
            }
        }
    }
}