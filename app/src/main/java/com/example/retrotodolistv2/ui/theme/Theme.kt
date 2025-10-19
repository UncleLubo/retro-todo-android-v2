package com.example.retrotodolistv2.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.example.retrotodolistv2.ui.theme.Typography

private val RetroColorScheme = darkColorScheme(
    primary      = RetroGreen,
    onPrimary    = RetroBlack,
    background   = RetroBlack,
    onBackground = RetroGreen,
    surface      = RetroBlack,
    onSurface    = RetroGreen,
    secondary    = RetroAccent,
    onSecondary  = RetroBlack
)

@Composable
fun RetroTodoListV2Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RetroColorScheme,
        typography  = RetroTypography,
        content     = content
    )
}