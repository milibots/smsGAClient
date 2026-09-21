// app/src/main/java/ir/smsgaclient/ui/common/BridgeStatusBar.kt
package ir.smsgaclient.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ir.smsgaclient.R
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.StatusConnectedForwarding
import ir.smsgaclient.ui.theme.StatusConnectedQueued
import ir.smsgaclient.ui.theme.StatusNotConfigured
import ir.smsgaclient.ui.theme.StatusNotForwarding
import ir.smsgaclient.ui.theme.TextPrimary

enum class BridgeStatus(val color: Color, val stringRes: Int) {
    CONNECTED_FORWARDING(StatusConnectedForwarding, R.string.status_connected_forwarding),
    CONNECTED_QUEUED(StatusConnectedQueued, R.string.status_connected_queued),
    NOT_FORWARDING(StatusNotForwarding, R.string.status_disconnected),
    NOT_CONFIGURED(StatusNotConfigured, R.string.status_not_configured)
}

@Composable
fun BridgeStatusBar(
    status: BridgeStatus,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(status.color)
        )
        Text(
            text = stringResource(status.stringRes),
            style = CockpitTypography.bodyMedium,
            color = TextPrimary
        )
    }
}
