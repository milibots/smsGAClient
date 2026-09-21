package ir.smsgaclient.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@Immutable
data class SmsGaCustomColors(
    val isDark: Boolean,
    val backgroundMidnight: Color,
    val surfaceDark: Color,
    val cardBackground: Color,
    val surfaceElevated: Color,
    val borderSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val pureWhite: Color,
    val pureBlack: Color,
    val silverPlatinum: Color
)

val DarkCustomColors = SmsGaCustomColors(
    isDark = true,
    backgroundMidnight = Color(0xFF000000),
    surfaceDark = Color(0xFF0A0A0C),
    cardBackground = Color(0xFF141416),
    surfaceElevated = Color(0xFF1E1E22),
    borderSubtle = Color(0xFF2E2E34),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFA1A1AA),
    textMuted = Color(0xFF71717A),
    pureWhite = Color(0xFFFFFFFF),
    pureBlack = Color(0xFF000000),
    silverPlatinum = Color(0xFFE5E5EA)
)

val LightCustomColors = SmsGaCustomColors(
    isDark = false,
    backgroundMidnight = Color(0xFFF6F7F9),
    surfaceDark = Color(0xFFFFFFFF),
    cardBackground = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFEDEDF2),
    borderSubtle = Color(0xFFE4E4E8),
    textPrimary = Color(0xFF0F1115),
    textSecondary = Color(0xFF52525B),
    textMuted = Color(0xFF71717A),
    pureWhite = Color(0xFF0F1115),
    pureBlack = Color(0xFFFFFFFF),
    silverPlatinum = Color(0xFF3F3F46)
)

val LocalSmsGaColors = staticCompositionLocalOf { DarkCustomColors }

object SmsGaTheme {
    val colors: SmsGaCustomColors
        @Composable
        get() = LocalSmsGaColors.current

    val isDark: Boolean
        @Composable
        get() = LocalSmsGaColors.current.isDark
}

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF1E1E22),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFFE5E5EA),
    onSecondary = Color(0xFF000000),
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF141416),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF1E1E22),
    onSurfaceVariant = Color(0xFFA1A1AA),
    outline = Color(0xFF2E2E34)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0F1115),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEDEDF2),
    onPrimaryContainer = Color(0xFF0F1115),
    secondary = Color(0xFF3F3F46),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF6F7F9),
    onBackground = Color(0xFF0F1115),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F1115),
    surfaceVariant = Color(0xFFEDEDF2),
    onSurfaceVariant = Color(0xFF52525B),
    outline = Color(0xFFE4E4E8)
)

@Composable
fun SmsGaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val customColors = if (darkTheme) DarkCustomColors else LightCustomColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl,
        LocalSmsGaColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = CockpitTypography,
            content = content
        )
    }
}
