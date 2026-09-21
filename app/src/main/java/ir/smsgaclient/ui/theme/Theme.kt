// app/src/main/java/ir/smsgaclient/ui/theme/Theme.kt
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
    primaryContainer = Navy800,
    onPrimaryContainer = TealPrimaryLight,
    secondary = BlueAccent,
    onSecondary = TextPrimary,
    background = SurfaceDark,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = Navy700,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle
)

/**
 * Persian-first Material 3 theme for smsGAClient Cockpit.
 * Forces LayoutDirection.Rtl by default.
 */
@Composable
fun SmsGaTheme(
    content: @Composable () -> Unit
) {
    // SYSTEM_PROMPT.md §0: Persian-first, RTL is the default
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = CockpitTypography,
            content = content
        )
    }
}
