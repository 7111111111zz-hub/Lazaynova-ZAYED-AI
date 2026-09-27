package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LazaynovaLightColorScheme = lightColorScheme(
    primary = LazaynovaPrimary,
    onPrimary = LazaynovaOnPrimary,
    primaryContainer = LazaynovaPrimaryContainer,
    onPrimaryContainer = LazaynovaPrimary,
    secondary = LazaynovaSecondary,
    onSecondary = LazaynovaOnSecondary,
    secondaryContainer = LazaynovaSecondaryContainer,
    onSecondaryContainer = LazaynovaSecondary,
    tertiary = LazaynovaTertiary,
    tertiaryContainer = LazaynovaTertiaryContainer,
    background = LazaynovaBackground,
    onBackground = LazaynovaTextPrimary,
    surface = LazaynovaSurface,
    onSurface = LazaynovaTextPrimary,
    surfaceVariant = LazaynovaSurfaceVariant,
    onSurfaceVariant = LazaynovaTextSecondary,
    outline = LazaynovaBorder
)

@Composable
fun LazaynovaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LazaynovaLightColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    LazaynovaTheme(content = content)
}
