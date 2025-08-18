package com.pc922.espaoilandroid.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun EspaoilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = EspaoilRed,
            onPrimary = Color.White,
            primaryContainer = EspaoilRedDark,
            onPrimaryContainer = Color.White,
            secondary = Teal200,
            onSecondary = Color.Black
        )
    } else {
        lightColorScheme(
            primary = EspaoilRed,
            onPrimary = Color.White,
            primaryContainer = EspaoilRed,
            onPrimaryContainer = Color.White,
            secondary = Teal200,
            onSecondary = Color.Black
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

