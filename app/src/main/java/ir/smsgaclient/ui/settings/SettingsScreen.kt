package ir.smsgaclient.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.smsgaclient.ui.theme.BackgroundMidnight
import ir.smsgaclient.ui.theme.BorderSubtle
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.CodeSnippetStyle
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.PureBlack
import ir.smsgaclient.ui.theme.PureWhite
import ir.smsgaclient.ui.theme.SilverPlatinum
import ir.smsgaclient.ui.theme.StatusConnectedForwarding
import ir.smsgaclient.ui.theme.StatusNotForwarding
import ir.smsgaclient.ui.theme.SurfaceElevated
import ir.smsgaclient.ui.theme.TextMuted
import ir.smsgaclient.ui.theme.TextPrimary
import ir.smsgaclient.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onTestSms: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var webhookInput by remember(uiState.webhookUrl) { mutableStateOf(uiState.webhookUrl) }
    var showRevokeSheet by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundMidnight)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text(
                text = "تنظیمات سامانه",
                style = CockpitTypography.headlineMedium,
                color = TextPrimary
            )
        }

        item {
            SettingsCard(
                icon = Icons.Default.CloudDone,
                title = "اتصال و وب‌هوک دریافت"
            ) {
                OutlinedTextField(
                    value = webhookInput,
                    onValueChange = {
                        webhookInput = it
                        viewModel.updateWebhookUrl(it)
                    },
                    label = { Text("آدرس وب‌هوک (الزاماً HTTPS)") },
                    placeholder = { Text("https://example.com/api/sms/webhook") },
                    isError = uiState.errorMessage != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        style = CockpitTypography.bodySmall,
                        color = StatusNotForwarding,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "وضعیت کلید احراز هویت",
                            style = CockpitTypography.titleSmall,
                            color = TextSecondary
                        )
                        Text(
                            text = uiState.apiTokenMasked.ifEmpty { "هنوز جفت‌سازی انجام نشده است" },
                            style = CodeSnippetStyle,
                            color = PureWhite
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurfaceElevated)
                            .border(1.dp, BorderSubtle, CircleShape)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onTestSms,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PureWhite,
                        contentColor = PureBlack
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = PureBlack,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "ارسال پیامک تستی به وب‌هوک", color = PureBlack, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            SettingsCard(
                icon = Icons.Default.FilterList,
                title = "فیلتر پیامک‌های بانکی"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "فقط واریزها ارسال شوند",
                            style = CockpitTypography.titleSmall,
                            color = TextPrimary
                        )
                        Text(
                            text = "پیامک‌های برداشت، کارمزد و پیام‌های تبلیغاتی نادیده گرفته می‌شوند.",
                            style = CockpitTypography.bodySmall,
                            color = TextMuted
                        )
                    }
                    Switch(
                        checked = uiState.onlyDeposits,
                        onCheckedChange = {  },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureBlack,
                            checkedTrackColor = PureWhite,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = SurfaceElevated
                        )
                    )
                }
            }
        }

        item {
            val context = androidx.compose.ui.platform.LocalContext.current
            val isBatteryExempt = remember {
                mutableStateOf(ir.smsgaclient.util.PermissionManager.isBatteryOptimizationIgnored(context))
            }

            SettingsCard(
                icon = Icons.Default.BatteryChargingFull,
                title = "سرویس پس‌زمینه و ماندگاری ۲۴ ساعته"
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "بهینه‌سازی باتری (Doze Mode)",
                            style = CockpitTypography.titleSmall,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isBatteryExempt.value) {
                                "مستثنی شده (سرویس در زمان خاموش بودن صفحه متوقف نمی‌شود)"
                            } else {
                                "بهینه‌سازی فعال است (ممکن است در خواب پیامک‌ها دریافت نشوند)"
                            },
                            style = CockpitTypography.bodySmall,
                            color = if (isBatteryExempt.value) PureWhite else SilverPlatinum
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceElevated)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isBatteryExempt.value) "تأیید شده ✓" else "محدود شده ⚠️",
                            style = CockpitTypography.labelSmall,
                            color = PureWhite
                        )
                    }
                }

                if (!isBatteryExempt.value) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            ir.smsgaclient.util.PermissionManager.requestIgnoreBatteryOptimization(context)
                            isBatteryExempt.value = ir.smsgaclient.util.PermissionManager.isBatteryOptimizationIgnored(context)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PureWhite,
                            contentColor = PureBlack
                        ),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Text(
                            text = "درخواست معافیت و فعالیت نامحدود باتری",
                            style = CockpitTypography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = PureBlack
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "شروع خودکار در گوشی (AutoStart)",
                        style = CockpitTypography.titleSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ir.smsgaclient.util.PermissionManager.getOemGuidanceText(),
                        style = CockpitTypography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            ir.smsgaclient.util.PermissionManager.openOemAutoStartSettings(context)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Text(
                            text = "تنظیم شروع خودکار گوشی",
                            style = CockpitTypography.labelMedium,
                            color = PureWhite
                        )
                    }
                }
            }
        }

        item {
            SettingsCard(
                icon = Icons.Default.Lock,
                title = "امنیت و اطلاعات دستگاه"
            ) {
                Text(
                    text = "رمزنگاری Keystore (AES256-GCM)",
                    style = CockpitTypography.titleSmall,
                    color = TextPrimary
                )
                Text(
                    text = "اطلاعات وب‌هوک و امضای دیجیتال با سخت‌افزار دستگاه محافظت می‌شوند.",
                    style = CockpitTypography.bodySmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "شناسه دستگاه (UUID):",
                    style = CockpitTypography.bodySmall,
                    color = TextSecondary
                )
                Text(
                    text = uiState.deviceId,
                    style = CodeSnippetStyle,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { showRevokeSheet = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SilverPlatinum),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Text(text = "قطع اتصال و لغو جفت‌سازی دستگاه")
                }
            }
        }
    }

    if (showRevokeSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()

        ModalBottomSheet(
            onDismissRequest = { showRevokeSheet = false },
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
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 16.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated)
                        .border(1.dp, BorderSubtle, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "لغو جفت‌سازی دستگاه؟",
                    style = CockpitTypography.titleLarge,
                    color = PureWhite
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "با لغو اتصال، کلیدهای احراز هویت حذف شده و برنامه تا جفت‌سازی مجدد پیامکی ارسال نخواهد کرد.",
                    style = CockpitTypography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showRevokeSheet = false
                            viewModel.revokePairing()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PureWhite,
                        contentColor = PureBlack
                    )
                ) {
                    Text(text = "تأیید و لغو", color = PureBlack, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showRevokeSheet = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Text(text = "انصراف", color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun SettingsCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated)
                        .border(1.dp, BorderSubtle, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = title,
                    style = CockpitTypography.titleMedium,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}
