package com.rahim.bankledger.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Violet600 = Color(0xFF7C3AED)
val Fuchsia500 = Color(0xFFD946EF)
val Orange400 = Color(0xFFFB923C)
val Emerald500 = Color(0xFF10B981)
val Teal500 = Color(0xFF14B8A6)
val Rose500 = Color(0xFFF43F5E)
val Pink500 = Color(0xFFEC4899)
val Amber400 = Color(0xFFFBBF24)
val Slate700 = Color(0xFF334155)
val Slate900 = Color(0xFF0F172A)

val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurface2 = Color(0xFFF1F5F9)
val LightText = Color(0xFF1E293B)
val LightTextSoft = Color(0xFF64748B)
val LightBorder = Color(0xFFE2E8F0)

val DarkBackground = Color(0xFF0B1120)
val DarkSurface = Color(0xFF1E293B)
val DarkSurface2 = Color(0xFF172033)
val DarkText = Color(0xFFF1F5F9)
val DarkTextSoft = Color(0xFF94A3B8)
val DarkBorder = Color(0xFF334155)

val HeroGradient = Brush.linearGradient(listOf(Violet600, Fuchsia500, Orange400))

data class LedgerColors(
    val background: Color,
    val surface: Color,
    val surface2: Color,
    val text: Color,
    val textSoft: Color,
    val border: Color
)

@Composable
fun rememberLedgerColors(): LedgerColors {
    val dark = isSystemInDarkTheme()
    return if (dark) {
        LedgerColors(DarkBackground, DarkSurface, DarkSurface2, DarkText, DarkTextSoft, DarkBorder)
    } else {
        LedgerColors(LightBackground, LightSurface, LightSurface2, LightText, LightTextSoft, LightBorder)
    }
}

@Composable
fun BankLedgerTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val scheme = if (dark) {
        darkColorScheme(primary = Violet600, secondary = Fuchsia500, tertiary = Orange400)
    } else {
        lightColorScheme(primary = Violet600, secondary = Fuchsia500, tertiary = Orange400)
    }
    MaterialTheme(colorScheme = scheme, content = content)
}
