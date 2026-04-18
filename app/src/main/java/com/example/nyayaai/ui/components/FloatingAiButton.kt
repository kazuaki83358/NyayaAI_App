package com.example.nyayaai.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.nyayaai.ui.theme.BrandIndigo

@Composable
fun FloatingAiButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = BrandIndigo,
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = Color.White
        )
    }
}