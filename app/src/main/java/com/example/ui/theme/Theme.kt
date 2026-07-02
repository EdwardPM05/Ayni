package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = Terracotta,
  secondary = Gold,
  tertiary = DarkBrown,
  background = Charcoal,
  surface = Color(0xFF2C2725),
  onPrimary = Color.White,
  onSecondary = Color.Black,
  onTertiary = Color.White,
  onBackground = WarmWhite,
  onSurface = WarmWhite
)

private val LightColorScheme = lightColorScheme(
  primary = Terracotta,
  secondary = Gold,
  tertiary = DarkBrown,
  background = SoftCream,
  surface = Color.White,
  onPrimary = Color.White,
  onSecondary = Color.White,
  onTertiary = Color.White,
  onBackground = Charcoal,
  onSurface = Charcoal
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set to false to enforce the beautiful brand identity
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
