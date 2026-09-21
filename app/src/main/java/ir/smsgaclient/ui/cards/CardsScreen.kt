// app/src/main/java/ir/smsgaclient/ui/cards/CardsScreen.kt
package ir.smsgaclient.ui.cards

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import ir.smsgaclient.ui.theme.Navy800
import ir.smsgaclient.ui.theme.StatusConnectedForwarding
import ir.smsgaclient.ui.theme.StatusConnectedQueued
import ir.smsgaclient.ui.theme.StatusNotForwarding
import ir.smsgaclient.ui.theme.SurfaceDark
import ir.smsgaclient.ui.theme.TealPrimary
import ir.smsgaclient.ui.theme.TealPrimaryLight
import ir.smsgaclient.ui.theme.TextMuted
import ir.smsgaclient.ui.theme.TextPrimary
import ir.smsgaclient.ui.theme.TextSecondary
import ir.smsgaclient.util.PersianNumberFormatter

@Composable
fun CardsScreen(
    viewModel: CardsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = TealPrimary,
                contentColor = TextPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "افزودن کارت")
            }
        },
        containerColor = SurfaceDark,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "مدیریت کارت‌ها و سقف روزانه",
                style = CockpitTypography.headlineMedium,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.cards.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Text(
                        text = "هنوز کارتی ثبت نشده است. با زدن دکمه + کارت بانکی خود را اضافه کنید.",
                        style = CockpitTypography.bodyMedium,
                        color = TextMuted,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.cards) { cardUsage ->
                        CardItemRow(cardUsage = cardUsage)
                    }
                }
            }
        }

        if (showAddDialog) {
            AddCardDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { bank, last4, holder, limit ->
                    viewModel.addCard(bank, last4, holder, limit)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun CardItemRow(cardUsage: CardWithUsage) {
    val card = cardUsage.card
    val progressColor = when {
        cardUsage.usagePercent >= 1.0f -> StatusNotForwarding   // Red (100%)
        cardUsage.usagePercent >= 0.9f -> StatusConnectedQueued // Yellow (90%)
        else -> StatusConnectedForwarding                       // Green
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "بانک ${card.bankId}",
                    style = CockpitTypography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = "•••• ${card.last4}",
                    style = CockpitTypography.titleMedium,
                    color = TealPrimaryLight
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = card.holderName,
                style = CockpitTypography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { cardUsage.usagePercent.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = progressColor,
                trackColor = Navy800
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "مصرف: ${PersianNumberFormatter.formatToman(cardUsage.usageRial / 10)}",
                    style = CockpitTypography.bodySmall ?: CockpitTypography.bodyMedium,
                    color = TextMuted
                )
                Text(
                    text = "سقف: ${PersianNumberFormatter.formatToman(card.dailyLimitRial / 10)}",
                    style = CockpitTypography.bodySmall ?: CockpitTypography.bodyMedium,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onConfirm: (bank: String, last4: String, holder: String, limit: Long) -> Unit
) {
    var bank by remember { mutableStateOf("blu") }
    var last4 by remember { mutableStateOf("") }
    var holder by remember { mutableStateOf("") }
    var limit by remember { mutableStateOf("500000000") } // 50 million Toman

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "افزودن کارت جدید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = bank,
                    onValueChange = { bank = it },
                    label = { Text("نام بانک") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = last4,
                    onValueChange = { if (it.length <= 4) last4 = it },
                    label = { Text("۴ رقم آخر کارت") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = holder,
                    onValueChange = { holder = it },
                    label = { Text("نام صاحب کارت") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = limit,
                    onValueChange = { limit = it },
                    label = { Text("سقف روزانه (ریال)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val limitVal = limit.toLongOrNull() ?: 0L
                    if (last4.length == 4) {
                        onConfirm(bank, last4, holder, limitVal)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text(text = "افزودن")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(text = "انصراف")
            }
        }
    )
}
