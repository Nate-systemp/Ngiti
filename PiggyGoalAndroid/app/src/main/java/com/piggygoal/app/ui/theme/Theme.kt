package com.piggygoal.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.piggygoal.app.domain.model.AppThemeMode

private val LightColors = lightColorScheme(
    primary = BrandPrimary,
    secondary = BrandSecondary,
    tertiary = BrandPrimary,
    surface = BrandSurfaceLight,
)

private val DarkColors = darkColorScheme(
    primary = BrandPrimaryDark,
    secondary = BrandSecondary,
    tertiary = BrandPrimaryDark,
    surface = BrandSurfaceDark,
)

@Composable
fun PiggyGoalTheme(
    themeMode: AppThemeMode,
    content: @Composable () -> Unit,
) {
    val isDarkMode = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val context = LocalContext.current
    val colors = if (themeMode == AppThemeMode.SYSTEM && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isDarkMode) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else if (isDarkMode) {
        DarkColors
    } else {
        LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content,
    )
}
