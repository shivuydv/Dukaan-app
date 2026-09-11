package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = WhatsAppTealGreen,
    onPrimary = Color.White,
    primaryContainer = WhatsAppDarkTeal,
    onPrimaryContainer = Color.White,
    secondary = KiranaAmberLight,
    onSecondary = Color.Black,
    tertiary = WhatsAppLightGreen,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = Color(0xFFE9EDEF),
    onSurface = Color(0xFFE9EDEF)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = WhatsAppDarkTeal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2F1),
    onPrimaryContainer = WhatsAppDarkTeal,
    secondary = KiranaAmber,
    onSecondary = Color.White,
    secondaryContainer = KiranaAmberContainer,
    onSecondaryContainer = KiranaAmber,
    tertiary = WhatsAppTealGreen,
    background = LightBackground,
    surface = LightSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary
  )

@Composable
fun DukaanVoiceTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent WhatsApp branding
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

