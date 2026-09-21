// app/src/main/java/ir/smsgaclient/ui/settings/SettingsScreen.kt
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
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.smsgaclient.ui.theme.BackgroundMidnight
import ir.smsgaclient.ui.theme.BorderSubtle
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.CodeSnippetStyle
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.Navy700
import ir.smsgaclient.ui.theme.Navy800
import ir.smsgaclient.ui.theme.StatusConnectedForwarding
import ir.smsgaclient.ui.theme.StatusNotForwarding
import ir.smsgaclient.ui.theme.SurfaceElevated
import ir.smsgaclient.ui.theme.TealAccent
import ir.smsgaclient.ui.theme.TealPrimary
import ir.smsgaclient.ui.theme.TextMuted
import ir.smsgaclient.ui.theme.TextPrimary
import ir.smsgaclient.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onTestSms: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var webhookInput by remember(uiState.webhookUrl) { mutableStateOf(uiState.webhookUrl) }
    var showRevokeConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundMidnight)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(
                text = "تنظیمات سامانه",
                style = CockpitTypography.headlineMedium,
                color = TextPrimary
            )
        }

        // Section 1: اتصال وب‌هوک
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
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealPrimary,
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

                Spacer(modifier = Modifier.height(12.dp))

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
                            color = TealAccent
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurfaceElevated)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = TealAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onTestSms,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "ارسال پیامک تستی به وب‌هوک", color = Color.White)
                }
            }
        }

        // Section 2: فیلترها و قوانین پردازش
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
                        onCheckedChange = { /* Toggle */ },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = TealPrimary
                        )
                    )
                }
            }
        }

        // Section 3: پایش و بهینه‌سازی باتری
        item {
            SettingsCard(
                icon = Icons.Default.BatteryChargingFull,
                title = "سرویس پس‌زمینه و ماندگاری"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "وضعیت بهینه‌سازی باتری (Doze)",
                            style = CockpitTypography.titleSmall,
                            color = TextPrimary
                        )
                        Text(
                            text = "استثنا شده (سرویس در خواب قطع نمی‌شود)",
                            style = CockpitTypography.bodySmall,
                            color = StatusConnectedForwarding
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x2210B981))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "تأیید شده",
                            style = CockpitTypography.labelSmall,
                            color = StatusConnectedForwarding
                        )
                    }
                }
            }
        }

        // Section 4: اطلاعات دستگاه و امنیت
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
                    onClick = { showRevokeConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusNotForwarding),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusNotForwarding.copy(alpha = 0.5f))
                ) {
                    Text(text = "قطع اتصال و لغو جفت‌سازی دستگاه")
                }
            }
        }
    }

    if (showRevokeConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRevokeConfirmDialog = false },
            title = {
                Text(
                    text = "لغو جفت‌سازی دستگاه؟",
                    style = CockpitTypography.titleLarge,
                    color = StatusNotForwarding
                )
            },
            text = {
                Text(
                    text = "با لغو اتصال، کلیدهای احراز هویت حذف شده و برنامه تا جفت‌سازی مجدد پیامکی ارسال نخواهد کرد.",
                    style = CockpitTypography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRevokeConfirmDialog = false
                        viewModel.revokePairing()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusNotForwarding)
                ) {
                    Text(text = "تأیید و لغو", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRevokeConfirmDialog = false }) {
                    Text(text = "انصراف", color = TextSecondary)
                }
            },
            containerColor = CardBackground,
            shape = RoundedCornerShape(16.dp)
        )
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TealAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = title,
                    style = CockpitTypography.titleMedium,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

