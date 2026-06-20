package com.example.nyayaai


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.nyayaai.data.PreferenceManager
import com.example.nyayaai.navigation.NavGraph
import com.example.nyayaai.ui.theme.NyayaAITheme
import com.example.nyayaai.ui.theme.ThemeManager
import kotlinx.coroutines.launch
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.nyayaai.worker.NotificationWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var preferenceManager: PreferenceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(this)
        
        setupBackgroundNotifications()

        setContent {
            val isDarkMode by preferenceManager.isDarkMode.collectAsState(initial = false)
            
            val themeManager = ThemeManager(
                initialIsDark = isDarkMode,
                onThemeChange = { dark ->
                    lifecycleScope.launch {
                        preferenceManager.setDarkMode(dark)
                    }
                }
            )

            NyayaAITheme(themeManager = themeManager) {
                NavGraph()
            }
        }
    }

    private fun setupBackgroundNotifications() {
        try {
            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(applicationContext).enqueue(workRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}