// app/src/main/java/ir/smsgaclient/ui/common/SmsGaBottomBar.kt
package ir.smsgaclient.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ir.smsgaclient.R
import ir.smsgaclient.ui.navigation.NavRoutes
import ir.smsgaclient.ui.theme.BorderSubtle
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.SurfaceElevated
import ir.smsgaclient.ui.theme.TealAccent
import ir.smsgaclient.ui.theme.TextMuted
import ir.smsgaclient.ui.theme.TextPrimary

sealed class BottomNavItem(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(NavRoutes.Home.route, R.string.tab_home, Icons.Default.Home)
    data object Transactions : BottomNavItem(NavRoutes.Transactions.route, R.string.tab_transactions, Icons.AutoMirrored.Filled.ReceiptLong)
    data object Cards : BottomNavItem(NavRoutes.Cards.route, R.string.tab_cards, Icons.Default.CreditCard)
    data object Settings : BottomNavItem(NavRoutes.Settings.route, R.string.tab_settings, Icons.Default.Settings)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Transactions,
    BottomNavItem.Cards,
    BottomNavItem.Settings
)

@Composable
fun SmsGaBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .border(
                width = 1.dp,
                color = BorderSubtle
            ),
        containerColor = CardBackground,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            val title = stringResource(item.titleRes)

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = title
                    )
                },
                label = {
                    Text(
                        text = title,
                        style = CockpitTypography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealAccent,
                    selectedTextColor = TealAccent,
                    indicatorColor = SurfaceElevated,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}

