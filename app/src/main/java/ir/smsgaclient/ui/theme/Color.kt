package ir.smsgaclient.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val StatusConnectedForwarding = Color(0xFFFFFFFF)
val StatusConnectedQueued = Color(0xFFD4D4D8)
val StatusNotForwarding = Color(0xFF71717A)
val StatusNotConfigured = Color(0xFF52525B)

val StatusForwardingContainer = Color(0xFF27272A)
val StatusForwardingOnContainer = Color(0xFFFFFFFF)

val StatusQueuedContainer = Color(0xFF1F1F23)
val StatusQueuedOnContainer = Color(0xFFE4E4E7)

val StatusErrorContainer = Color(0xFF18181B)
val StatusErrorOnContainer = Color(0xFFA1A1AA)

val StatusNeutralContainer = Color(0xFF141416)
val StatusNeutralOnContainer = Color(0xFFD4D4D8)

// Absolute colors when literal static white/black is strictly required
val AbsoluteWhite = Color(0xFFFFFFFF)
val AbsoluteBlack = Color(0xFF000000)

// Dynamic theme-adaptive color tokens
val PureWhite: Color
    @Composable
    get() = SmsGaTheme.colors.pureWhite

val PureBlack: Color
    @Composable
    get() = SmsGaTheme.colors.pureBlack

val BackgroundMidnight: Color
    @Composable
    get() = SmsGaTheme.colors.backgroundMidnight

val SurfaceDark: Color
    @Composable
    get() = SmsGaTheme.colors.surfaceDark

val CardBackground: Color
    @Composable
    get() = SmsGaTheme.colors.cardBackground

val SurfaceElevated: Color
    @Composable
    get() = SmsGaTheme.colors.surfaceElevated

val BorderSubtle: Color
    @Composable
    get() = SmsGaTheme.colors.borderSubtle

val TextPrimary: Color
    @Composable
    get() = SmsGaTheme.colors.textPrimary

val TextSecondary: Color
    @Composable
    get() = SmsGaTheme.colors.textSecondary

val TextMuted: Color
    @Composable
    get() = SmsGaTheme.colors.textMuted

val SilverPlatinum: Color
    @Composable
    get() = SmsGaTheme.colors.silverPlatinum

val GrayTextSecondary: Color
    @Composable
    get() = SmsGaTheme.colors.textSecondary

val GrayTextMuted: Color
    @Composable
    get() = SmsGaTheme.colors.textMuted

// Legacy aliases for backward compatibility
val Navy950: Color @Composable get() = BackgroundMidnight
val Navy900: Color @Composable get() = SurfaceDark
val Navy800: Color @Composable get() = CardBackground
val Navy750: Color @Composable get() = SurfaceElevated
val Navy700: Color @Composable get() = BorderSubtle
val Navy600: Color @Composable get() = BorderSubtle

val TealPrimary: Color @Composable get() = PureWhite
val TealPrimaryLight: Color @Composable get() = SilverPlatinum
val TealAccent: Color @Composable get() = PureWhite
val AmberGold: Color @Composable get() = SilverPlatinum
val BlueAccent: Color @Composable get() = TextSecondary

// Bank Card styling (consistent luxury card appearance)
val BankBlu = Color(0xFF22252A)
val BankPasargad = Color(0xFF191A1D)
val BankSaman = Color(0xFF2A2D33)
val BankMellat = Color(0xFF1E2024)
val BankMelli = Color(0xFF17181B)
val BankSepah = Color(0xFF24262C)
val BankTejarat = Color(0xFF202328)
val BankKeshavarzi = Color(0xFF1B1D21)
val BankParsian = Color(0xFF282A30)
val BankRefah = Color(0xFF181A1E)
val BankShahr = Color(0xFF26282E)
val BankSaderat = Color(0xFF1A1C20)
val BankAyandeh = Color(0xFF25272D)
val BankDefault = Color(0xFF1F2126)
