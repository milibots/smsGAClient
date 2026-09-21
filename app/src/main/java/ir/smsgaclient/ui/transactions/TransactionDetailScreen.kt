package ir.smsgaclient.ui.transactions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.smsgaclient.data.db.entity.ParsedSmsEntity
import ir.smsgaclient.data.db.entity.RawSmsEntity
import ir.smsgaclient.data.db.entity.TransactionEntity
import ir.smsgaclient.ui.common.BankAvatar
import ir.smsgaclient.ui.common.StatusPill
import ir.smsgaclient.ui.common.getBankInfo
import ir.smsgaclient.ui.theme.BackgroundMidnight
import ir.smsgaclient.ui.theme.BorderSubtle
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.CodeSnippetStyle
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.CurrencyUnitStyle
import ir.smsgaclient.ui.theme.Navy800
import ir.smsgaclient.ui.theme.SurfaceElevated
import ir.smsgaclient.ui.theme.TealAccent
import ir.smsgaclient.ui.theme.TealPrimary
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
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundMidnight)
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "بازگشت",
                    tint = TextPrimary
                )
            }
            Text(
                text = "رسید و جزئیات تراکنش",
                style = CockpitTypography.titleLarge,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (transaction == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "اطلاعات تراکنش در دسترس نیست",
                    style = CockpitTypography.bodyMedium,
                    color = TextMuted
                )
            }
            return@Column
        }

        val bankInfo = getBankInfo(transaction.bankId)

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(Navy800, Color(0xFF132F3D), Navy800)
                                )
                            )
                            .padding(22.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "مبلغ واریز شده",
                                    style = CockpitTypography.titleSmall,
                                    color = TextSecondary
                                )
                                StatusPill(status = transaction.status)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "+ ${PersianNumberFormatter.formatNumber(transaction.amountRial / 10)}",
                                    style = CockpitTypography.headlineLarge.copy(fontSize = 32.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = TealAccent
                                )
                                Text(
                                    text = "تومان",
                                    style = CurrencyUnitStyle.copy(fontSize = 16.sp),
                                    color = TealAccent,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }

                            Text(
                                text = "معادل: ${PersianNumberFormatter.formatRial(transaction.amountRial)}",
                                style = CockpitTypography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "مشخصات بانکی واریز",
                            style = CockpitTypography.titleMedium,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        DetailRow(
                            label = "بانک مبدا / مقصد",
                            value = bankInfo.nameFa,
                            leadingAvatar = { BankAvatar(bankId = transaction.bankId, size = 28.dp) }
                        )

                        val cardText = transaction.cardLast4?.let { "•••• ${PersianNumberFormatter.toPersianDigits(it)}" } ?: "نامشخص"
                        DetailRow(label = "شماره کارت مقصد", value = cardText)

                        if (parsedSms?.balanceRial != null && parsedSms.balanceRial > 0) {
                            DetailRow(
                                label = "موجودی پس از تراکنش",
                                value = PersianNumberFormatter.formatToman(parsedSms.balanceRial / 10)
                            )
                        }

                        DetailRow(
                            label = "شناسه یکتا (MessageId)",
                            value = transaction.messageId.take(16) + "...",
                            trailingAction = {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Message ID", transaction.messageId))
                                        Toast.makeText(context, "شناسه در حافظه کپی شد", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "کپی",
                                        tint = TealAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "متن خام پیامک دریافتی",
                                style = CockpitTypography.titleMedium,
                                color = TextPrimary
                            )
                            val smsText = rawSms?.body ?: ""
                            if (smsText.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Raw SMS", smsText))
                                        Toast.makeText(context, "متن پیامک کپی شد", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "کپی پیامک",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceElevated)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = rawSms?.body ?: "پیامک خام در دیتابیس ثبت نشده است",
                                style = CodeSnippetStyle,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onRetryForward(transaction.messageId) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "ارسال مجدد به وب‌هوک", color = Color.White)
            }

            OutlinedButton(
                onClick = {
                    Toast.makeText(context, "گزارش خطای خواندن ثبت گردید", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Text(text = "گزارش خطا")
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    leadingAvatar: (@Composable () -> Unit)? = null,
    trailingAction: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = CockpitTypography.bodyMedium,
            color = TextMuted
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leadingAvatar?.invoke()
            Text(
                text = value,
                style = CockpitTypography.titleSmall,
                color = TextPrimary
            )
            trailingAction?.invoke()
        }
    }
}

