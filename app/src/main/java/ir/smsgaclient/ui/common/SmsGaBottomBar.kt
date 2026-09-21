// app/src/main/java/ir/smsgaclient/ui/common/SmsGaBottomBar.kt
package ir.smsgaclient.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
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
import ir.smsgaclient.R
import ir.smsgaclient.ui.navigation.NavRoutes
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.Navy800
import ir.smsgaclient.ui.theme.TealPrimary
import ir.smsgaclient.ui.theme.TextMuted
import ir.smsgaclient.ui.theme.TextPrimary

sealed class BottomNavItem(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(NavRoutes.Home.route, R.string.tab_home, Icons.Default.Home)
    data object Transactions : BottomNavItem(NavRoutes.Transactions.route, R.string.tab_transactions, Icons.Default.ReceiptLong)
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
        modifier = modifier,
        containerColor = CardBackground
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
                label = { Text(text = title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = Navy800,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}
