package ir.smsgaclient.ui.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ir.smsgaclient.R
import ir.smsgaclient.ui.theme.BorderSubtle
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.StatusConnectedForwarding
import ir.smsgaclient.ui.theme.StatusConnectedQueued
import ir.smsgaclient.ui.theme.StatusNotConfigured
import ir.smsgaclient.ui.theme.StatusNotForwarding
import ir.smsgaclient.ui.theme.TextMuted
import ir.smsgaclient.ui.theme.TextPrimary

enum class BridgeStatus(val color: Color, val stringRes: Int, val descriptionFa: String) {
    CONNECTED_FORWARDING(
        StatusConnectedForwarding,
        R.string.status_connected_forwarding,
        "پل ارتباطی فعال و آماده دریافت پیامک بانکی"
    ),
    CONNECTED_QUEUED(
        StatusConnectedQueued,
        R.string.status_connected_queued,
        "پیامک‌ها در صف انتظار ارسال به وب‌هوک"
    ),
    NOT_FORWARDING(
        StatusNotForwarding,
        R.string.status_disconnected,
        "ارسال متوقف شده — خطای اتصال یا کمبود باتری"
    ),
    NOT_CONFIGURED(
        StatusNotConfigured,
        R.string.status_not_configured,
        "آدرس وب‌هوک یا کلید احراز هویت تنظیم نشده است"
    )
}

@Composable
fun BridgeStatusBar(
    status: BridgeStatus,
    modifier: Modifier = Modifier
) {
    val isPulsing = status == BridgeStatus.CONNECTED_FORWARDING
    val transition = rememberInfiniteTransition(label = "pulseTransition")

    val pulseScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPulsing) 1.8f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )

    val pulseAlpha by transition.animateFloat(
        initialValue = if (isPulsing) 0.5f else 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(CardBackground)
            .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier.size(22.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isPulsing) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                            alpha = pulseAlpha
                        }
                        .clip(CircleShape)
                        .background(status.color)
                )
            }
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(status.color)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(status.stringRes),
                style = CockpitTypography.titleSmall,
                color = TextPrimary
            )
            Text(
                text = status.descriptionFa,
                style = CockpitTypography.bodySmall,
                color = TextMuted
            )
        }
    }
}
