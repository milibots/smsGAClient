package ir.smsgaclient.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.SmsFailed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.smsgaclient.ui.theme.BorderSubtle
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.GrayTextSecondary
import ir.smsgaclient.ui.theme.PureBlack
import ir.smsgaclient.ui.theme.PureWhite
import ir.smsgaclient.ui.theme.SurfaceElevated
import ir.smsgaclient.ui.theme.TextPrimary
import ir.smsgaclient.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun BackgroundPermissionWarningBanner(
    isSmsMissing: Boolean,
    isBatteryOptimized: Boolean,
    onResolveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isSmsMissing || isBatteryOptimized,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .clickable { onResolveClick() },
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF18181C)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(0xFF383842)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF282830)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSmsMissing) Icons.Default.SmsFailed else Icons.Default.BatteryAlert,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isSmsMissing) {
                            "دسترسی پیامک غیرفعال است!"
                        } else {
                            "بهینه‌سازی باتری مانع اجرای مداوم است"
                        },
                        style = CockpitTypography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isSmsMissing) {
                            "پیامک‌های بانکی واریزی دریافت نخواهند شد."
                        } else {
                            "برای دریافت بدون قطعی در زمان خاموش بودن صفحه، لمس کنید."
                        },
                        style = CockpitTypography.bodySmall,
                        color = GrayTextSecondary,
                        lineHeight = 18.sp
                    )
                }

                Button(
                    onClick = onResolveClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PureWhite,
                        contentColor = PureBlack
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "رفع مشکل",
                        style = CockpitTypography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = PureBlack
                    )
                }
            }
        }
    }
}

@Composable
fun BackgroundSetupModalDialog(
    hasSmsPermission: Boolean,
    isBatteryExempted: Boolean,
    onRequestSms: () -> Unit,
    onRequestBatteryExemption: () -> Unit,
    onOpenOemSettings: () -> Unit,
    onDismiss: () -> Unit,
    oemGuidance: String
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardBackground,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderSubtle)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 16.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF2E2E36), Color(0xFF141416))
                        )
                    )
                    .border(1.dp, Color(0xFF42424E), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "تنظیم دریافت مداوم در پس‌زمینه",
                style = CockpitTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "برای اینکه حتی هنگام خاموش بودن نمایشگر یا در طول شب، هیچ پیامک واریزی از دست نرود، نیازمند مجوزهای زیر هستیم:",
                style = CockpitTypography.bodyMedium,
                color = TextSecondary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            SetupStepItem(
                icon = Icons.Default.Sms,
                title = "۱. مجوز خواندن پیامک‌ها (SMS)",
                description = "برای دریافت خودکار واریزی‌ها (هیچ داده‌ای به غیر از وب‌هوک شما ارسال نمی‌شود).",
                isGranted = hasSmsPermission,
                buttonText = "اعطای مجوز پیامک",
                onAction = onRequestSms
            )

            Spacer(modifier = Modifier.height(12.dp))

            SetupStepItem(
                icon = Icons.Default.BatteryChargingFull,
                title = "۲. عدم بهینه‌سازی باتری (نامحدود)",
                description = "جلوگیری از قطع سرویس و بستن برنامه توسط اندروید در حالت خواب عمیق (Doze).",
                isGranted = isBatteryExempted,
                buttonText = "رفع محدودیت باتری",
                onAction = onRequestBatteryExemption
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "شروع خودکار در گوشی شما",
                            style = CockpitTypography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = oemGuidance,
                        style = CockpitTypography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onOpenOemSettings,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Text(
                            text = "باز کردن تنظیمات اختصاصی دستگاه",
                            style = CockpitTypography.labelMedium,
                            color = PureWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasSmsPermission && isBatteryExempted) PureWhite else SurfaceElevated,
                    contentColor = if (hasSmsPermission && isBatteryExempted) PureBlack else TextPrimary
                )
            ) {
                Text(
                    text = if (hasSmsPermission && isBatteryExempted) "عالی شد، بستن پنجره" else "متوجه شدم (بعداً تنظیم می‌کنم)",
                    style = CockpitTypography.labelLarge,
                    color = if (hasSmsPermission && isBatteryExempted) PureBlack else TextPrimary
                )
            }
        }
    }
}

@Composable
private fun SetupStepItem(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    buttonText: String,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isGranted) PureWhite else GrayTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = title,
                        style = CockpitTypography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                if (isGranted) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF282830))
                            .border(1.dp, Color(0xFF42424E), RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "فعال شد ✓",
                            style = CockpitTypography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PureWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = CockpitTypography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            if (!isGranted) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onAction,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PureWhite,
                        contentColor = PureBlack
                    )
                ) {
                    Text(
                        text = buttonText,
                        style = CockpitTypography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = PureBlack
                    )
                }
            }
        }
    }
}
