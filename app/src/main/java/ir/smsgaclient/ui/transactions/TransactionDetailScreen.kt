// app/src/main/java/ir/smsgaclient/ui/transactions/TransactionDetailScreen.kt
package ir.smsgaclient.ui.transactions

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.smsgaclient.data.db.entity.ParsedSmsEntity
import ir.smsgaclient.data.db.entity.RawSmsEntity
import ir.smsgaclient.data.db.entity.TransactionEntity
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.Navy800
import ir.smsgaclient.ui.theme.SurfaceDark
import ir.smsgaclient.ui.theme.TealPrimary
import ir.smsgaclient.ui.theme.TealPrimaryLight
import ir.smsgaclient.ui.theme.TextMuted
import ir.smsgaclient.ui.theme.TextPrimary
import ir.smsgaclient.ui.theme.TextSecondary
import ir.smsgaclient.util.PersianNumberFormatter

@Composable
fun TransactionDetailScreen(
    transaction: TransactionEntity?,
    rawSms: RawSmsEntity?,
    parsedSms: ParsedSmsEntity?,
    onBack: () -> Unit,
    onRetryForward: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "بازگشت",
                    tint = TextPrimary
                )
            }
            Text(
                text = "جزئیات تراکنش",
                style = CockpitTypography.titleLarge,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (transaction == null) {
            Text(
                text = "تراکنش یافت نشد",
                style = CockpitTypography.bodyMedium,
                color = TextMuted
            )
            return@Column
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Amount Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "مبلغ واریزی",
                            style = CockpitTypography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = PersianNumberFormatter.formatToman(transaction.amountRial / 10),
                            style = CockpitTypography.headlineLarge,
                            color = TealPrimaryLight
                        )
                        Text(
                            text = PersianNumberFormatter.formatRial(transaction.amountRial),
                            style = CockpitTypography.bodySmall ?: CockpitTypography.bodyMedium,
                            color = TextMuted
                        )
                    }
                }
            }

            // Raw SMS Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "متن خام پیامک دریافتی",
                            style = CockpitTypography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = rawSms?.body ?: "در دسترس نیست",
                            style = CockpitTypography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Parsed JSON Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "اطلاعات استخراج شده",
                            style = CockpitTypography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "بانک: ${transaction.bankId}",
                            style = CockpitTypography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "کارت مقصد: ****${transaction.cardLast4 ?: "---"}",
                            style = CockpitTypography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "شناسه یکتا: ${transaction.messageId}",
                            style = CockpitTypography.bodySmall ?: CockpitTypography.bodyMedium,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onRetryForward(transaction.messageId) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Text(text = "ارسال مجدد به وب‌هوک")
            }

            OutlinedButton(
                onClick = { /* Report wrong parse */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Text(text = "گزارش خطا")
            }
        }
    }
}
