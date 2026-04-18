package com.example.nyayaai.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BrandIndigo,
    secondary = BrandPurple,
    tertiary = FamilyRose,
    background = BackgroundDark,
    surface = CardDark,
    onPrimary = Color.White,
    onBackground = TextLight,
    onSurface = TextLight
)

private val LightColorScheme = lightColorScheme(
    primary = BrandIndigo,
    secondary = BrandPurple,
    tertiary = FamilyRose,
    background = BackgroundLight,
    surface = CardWhite,
    onPrimary = Color.White,
    onBackground = TextDark,
    onSurface = TextDark
)

val LocalThemeManager = staticCompositionLocalOf {
    ThemeManager()
}

class ThemeManager(
    initialIsDark: Boolean = false,
    private val onThemeChange: (Boolean) -> Unit = {}
) {
    var isDark = mutableStateOf(initialIsDark)
    fun toggleTheme() {
        isDark.value = !isDark.value
        onThemeChange(isDark.value)
    }
}

@Composable
fun NyayaAITheme(
    themeManager: ThemeManager = LocalThemeManager.current,
    content: @Composable () -> Unit
) {
    val darkTheme = themeManager.isDark.value
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalThemeManager provides themeManager) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}