// app/src/main/java/ir/smsgaclient/ui/settings/SettingsScreen.kt
package ir.smsgaclient.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.Navy700
import ir.smsgaclient.ui.theme.StatusNotForwarding
import ir.smsgaclient.ui.theme.SurfaceDark
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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Text(
                text = "تنظیمات",
                style = CockpitTypography.headlineMedium,
                color = TextPrimary
            )
        }

        // Section 1: اتصال (Connection)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "اتصال و وب‌هوک",
                        style = CockpitTypography.titleMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = webhookInput,
                        onValueChange = {
                            webhookInput = it
                            viewModel.updateWebhookUrl(it)
                        },
                        label = { Text("آدرس وب‌هوک (HTTPS)") },
                        isError = uiState.errorMessage != null,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = Navy700
                        )
                    )

                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            style = CockpitTypography.bodySmall ?: CockpitTypography.bodyMedium,
                            color = StatusNotForwarding,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "کلید احراز هویت: ${uiState.apiTokenMasked.ifEmpty { "پیکربندی نشده" }}",
                        style = CockpitTypography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onTestSms,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text(text = "ارسال پیامک آزمایشی به وب‌هوک")
                    }
                }
            }
        }

        // Section 2: پرداخت و فیلترها
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "فیلتر پرداخت‌ها",
                        style = CockpitTypography.titleMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "فقط واریزها ارسال شوند",
                            style = CockpitTypography.bodyMedium,
                            color = TextPrimary
                        )
                        Switch(
                            checked = uiState.onlyDeposits,
                            onCheckedChange = { /* Toggle */ },
                            colors = SwitchDefaults.colors(checkedThumbColor = TealPrimary)
                        )
                    }
                }
            }
        }

        // Section 3: دستگاه و ابطال جفت‌سازی
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "اطلاعات دستگاه",
                        style = CockpitTypography.titleMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "شناسه دستگاه: ${uiState.deviceId}",
                        style = CockpitTypography.bodySmall ?: CockpitTypography.bodyMedium,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = { viewModel.revokePairing() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusNotForwarding)
                    ) {
                        Text(text = "قطع اتصال و لغو جفت‌سازی دستگاه")
                    }
                }
            }
        }
    }
}
