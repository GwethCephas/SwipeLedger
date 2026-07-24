package com.cephcoding.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkTealPrimary,
    onPrimary = ObsidianBg, // Dark text on bright primary
    primaryContainer = SteelBlue,
    onPrimaryContainer = TextHighEmphasis,

    secondary = SteelBlue,
    onSecondary = TextMediumEmphasis,

    tertiary = BrightCyanAccent, // Accent is mapped to Tertiary
    onTertiary = ObsidianBg,

    background = ObsidianBg,
    onBackground = TextHighEmphasis,

    surface = DarkCharcoal, // Card & popover colors
    onSurface = TextHighEmphasis,
    surfaceVariant = SlateGray, // Muted colors mapping
    onSurfaceVariant = TextMuted,

    error = CoralDestructive,
    onError = onError,

    outline = DarkTealPrimary.copy(alpha = 0.15f) // Matches your border
)

private val LightColorScheme = lightColorScheme(
    primary = DarkTealPrimary,
    onPrimary = ObsidianBg, // Dark text on bright primary
    primaryContainer = SteelBlue,
    onPrimaryContainer = TextHighEmphasis,

    secondary = SteelBlue,
    onSecondary = TextMediumEmphasis,

    tertiary = BrightCyanAccent, // Accent is mapped to Tertiary
    onTertiary = ObsidianBg,

    background = ObsidianBg,
    onBackground = TextHighEmphasis,

    surface = DarkCharcoal, // Card & popover colors
    onSurface = TextHighEmphasis,
    surfaceVariant = SlateGray, // Muted colors mapping
    onSurfaceVariant = TextMuted,

    error = CoralDestructive,
    onError = onError,

    outline = DarkTealPrimary.copy(alpha = 0.15f) // Matches your border
)

@Composable
fun SwipeLedgerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = swipeLedgerTypography,
        content = content
    )
}