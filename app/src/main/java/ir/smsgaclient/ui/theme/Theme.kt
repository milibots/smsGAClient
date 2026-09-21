package ir.smsgaclient.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimary,
    onPrimary = TextPrimary,
    primaryContainer = SurfaceElevated,
    onPrimaryContainer = TealAccent,
    secondary = AmberGold,
    onSecondary = TextPrimary,
    background = BackgroundMidnight,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle
)

@Composable
fun SmsGaTheme(
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = CockpitTypography,
            content = content
        )
    }
}
