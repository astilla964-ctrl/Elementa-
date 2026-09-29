package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

enum class AppThemeMode(val title: String, val icon: String) {
    LIGHT("Light", "☀️"),
    DARK("Dark", "🌙"),
    CREAM("Cream", "🍨"),
    AMBER("Amber", "🛢️")
}

private val LightPalette: ColorScheme = lightColorScheme(
    primary = LabLightPrimary,
    onPrimary = LabLightOnPrimary,
    primaryContainer = LabLightPrimaryContainer,
    onPrimaryContainer = LabLightOnPrimaryContainer,
    secondary = LabLightSecondary,
    onSecondary = LabLightOnSecondary,
    background = LabLightBackground,
    onBackground = LabLightOnBackground,
    surface = LabLightSurface,
    onSurface = LabLightOnSurface,
    surfaceVariant = LabLightSurfaceVariant,
    onSurfaceVariant = LabLightOnSurfaceVariant,
    outline = LabLightOutline
)

private val DarkPalette: ColorScheme = darkColorScheme(
    primary = LabDarkPrimary,
    onPrimary = LabDarkOnPrimary,
    primaryContainer = LabDarkPrimaryContainer,
    onPrimaryContainer = LabDarkOnPrimaryContainer,
    secondary = LabDarkSecondary,
    onSecondary = LabDarkOnSecondary,
    background = LabDarkBackground,
    onBackground = LabDarkOnBackground,
    surface = LabDarkSurface,
    onSurface = LabDarkOnSurface,
    surfaceVariant = LabDarkSurfaceVariant,
    onSurfaceVariant = LabDarkOnSurfaceVariant,
    outline = LabDarkOutline
)

private val CreamPalette: ColorScheme = lightColorScheme(
    primary = LabCreamPrimary,
    onPrimary = LabCreamOnPrimary,
    primaryContainer = LabCreamPrimaryContainer,
    onPrimaryContainer = LabCreamOnPrimaryContainer,
    secondary = LabCreamSecondary,
    onSecondary = LabCreamOnSecondary,
    background = LabCreamBackground,
    onBackground = LabCreamOnBackground,
    surface = LabCreamSurface,
    onSurface = LabCreamOnSurface,
    surfaceVariant = LabCreamSurfaceVariant,
    onSurfaceVariant = LabCreamOnSurfaceVariant,
    outline = LabCreamOutline
)

private val AmberPalette: ColorScheme = darkColorScheme(
    primary = LabAmberPrimary,
    onPrimary = LabAmberOnPrimary,
    primaryContainer = LabAmberPrimaryContainer,
    onPrimaryContainer = LabAmberOnPrimaryContainer,
    secondary = LabAmberSecondary,
    onSecondary = LabAmberOnSecondary,
    background = LabAmberBackground,
    onBackground = LabAmberOnBackground,
    surface = LabAmberSurface,
    onSurface = LabAmberOnSurface,
    surfaceVariant = LabAmberSurfaceVariant,
    onSurfaceVariant = LabAmberOnSurfaceVariant,
    outline = LabAmberOutline
)

@Composable
fun ElementaTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.LIGHT -> LightPalette
        AppThemeMode.DARK -> DarkPalette
        AppThemeMode.CREAM -> CreamPalette
        AppThemeMode.AMBER -> AmberPalette
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
