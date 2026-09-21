// app/src/main/java/ir/smsgaclient/ui/theme/Color.kt
package ir.smsgaclient.ui.theme

import androidx.compose.ui.graphics.Color

// Cockpit Status Colors (SYSTEM_PROMPT.md §12.1 + ui-ux-pro-max high-contrast pairs)
val StatusConnectedForwarding = Color(0xFF10B981) // Green / Emerald
val StatusConnectedQueued = Color(0xFFF59E0B)     // Amber / Yellow
val StatusNotForwarding = Color(0xFFEF4444)       // Red / Destructive
val StatusNotConfigured = Color(0xFF64748B)       // Slate Gray

// Status Container Pairs for badges and chips
val StatusForwardingContainer = Color(0xFF064E3B)
val StatusForwardingOnContainer = Color(0xFFA7F3D0)

val StatusQueuedContainer = Color(0xFF78350F)
val StatusQueuedOnContainer = Color(0xFFFDE68A)

val StatusErrorContainer = Color(0xFF7F1D1D)
val StatusErrorOnContainer = Color(0xFFFECACA)

val StatusNeutralContainer = Color(0xFF1E293B)
val StatusNeutralOnContainer = Color(0xFFCBD5E1)

// Primary Brand Palette (Persian Fintech Cockpit)
val Navy950 = Color(0xFF0A0F1D) // Deep OLED midnight background
val Navy900 = Color(0xFF0F172A)
val Navy800 = Color(0xFF162032) // Primary card background
val Navy750 = Color(0xFF1E2B42) // Elevated surface
val Navy700 = Color(0xFF28354A) // Borders & subtle dividers
val Navy600 = Color(0xFF334155)

val TealPrimary = Color(0xFF0D9488)      // Teal 600
val TealPrimaryLight = Color(0xFF14B8A6) // Teal 500
val TealAccent = Color(0xFF2DD4BF)       // Teal 400
val AmberGold = Color(0xFFF59E0B)        // Financial metric accent
val BlueAccent = Color(0xFF2563EB)

// Surface & Typography Tokens
val BackgroundMidnight = Navy950
val SurfaceDark = Navy900
val CardBackground = Navy800
val SurfaceElevated = Navy750
val BorderSubtle = Navy700

val TextPrimary = Color(0xFFF8FAFC)   // Slate 50 (> 14:1 contrast on Navy950)
val TextSecondary = Color(0xFF94A3B8) // Slate 400 (> 6:1 contrast)
val TextMuted = Color(0xFF64748B)     // Slate 500 (> 4.5:1 contrast)

// Iranian Bank Palette (for realistic bank card aesthetics & bank badges)
val BankBlu = Color(0xFF00A3FF)
val BankPasargad = Color(0xFFD4AF37)
val BankSaman = Color(0xFF1877F2)
val BankMellat = Color(0xFFE11D48)
val BankMelli = Color(0xFF0B5B8A)
val BankSepah = Color(0xFFD97706)
val BankTejarat = Color(0xFF0284C7)
val BankKeshavarzi = Color(0xFF16A34A)
val BankParsian = Color(0xFF831843)
val BankRefah = Color(0xFF059669)
val BankShahr = Color(0xFFDC2626)
val BankSaderat = Color(0xFF1E3A8A)
val BankAyandeh = Color(0xFF9333EA)
val BankDefault = Color(0xFF475569)
