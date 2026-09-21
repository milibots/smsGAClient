package ir.smsgaclient.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.smsgaclient.R
import ir.smsgaclient.ui.navigation.NavRoutes
import ir.smsgaclient.ui.theme.BorderSubtle
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.PureBlack
import ir.smsgaclient.ui.theme.PureWhite
import ir.smsgaclient.ui.theme.TextMuted

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
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp, bottom = 14.dp, top = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp),
            shape = RoundedCornerShape(34.dp),
            color = CardBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { item ->
                    val selected = currentRoute == item.route
                    val title = stringResource(item.titleRes)

                    val backgroundColor by animateColorAsState(
                        targetValue = if (selected) PureWhite else Color.Transparent,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "pillBgColor"
                    )
                    val contentColor by animateColorAsState(
                        targetValue = if (selected) PureBlack else TextMuted,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "pillContentColor"
                    )
                    val scale by animateFloatAsState(
                        targetValue = if (selected) 1.05f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        label = "pillScale"
                    )

                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clip(RoundedCornerShape(26.dp))
                            .background(backgroundColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onNavigate(item.route)
                            }
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = title,
                                tint = contentColor
                            )
                            AnimatedVisibility(
                                visible = selected,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    text = title,
                                    color = contentColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
