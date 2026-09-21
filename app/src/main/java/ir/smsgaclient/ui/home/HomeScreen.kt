// app/src/main/java/ir/smsgaclient/ui/home/HomeScreen.kt
package ir.smsgaclient.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.ScheduleSend
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.smsgaclient.data.db.entity.TransactionEntity
import ir.smsgaclient.ui.common.BankAvatar
import ir.smsgaclient.ui.common.BridgeStatusBar
import ir.smsgaclient.ui.common.StatusPill
import ir.smsgaclient.ui.common.getBankInfo
import ir.smsgaclient.ui.theme.AmberGold
import ir.smsgaclient.ui.theme.BackgroundMidnight
import ir.smsgaclient.ui.theme.BorderSubtle
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.CurrencyUnitStyle
import ir.smsgaclient.ui.theme.Navy800
import ir.smsgaclient.ui.theme.SurfaceElevated
import ir.smsgaclient.ui.theme.TealAccent
import ir.smsgaclient.ui.theme.TealPrimary
import ir.smsgaclient.ui.theme.TealPrimaryLight
import ir.smsgaclient.ui.theme.TextMuted
import ir.smsgaclient.ui.theme.TextPrimary
import ir.smsgaclient.ui.theme.TextSecondary
import ir.smsgaclient.util.PersianNumberFormatter
import kotlin.math.max

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToTransactions: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundMidnight)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Bridge Status Indicator Bar with Live Pulsing Dot
        item {
            BridgeStatusBar(status = uiState.bridgeStatus)
        }

        // 2. Hero Card: Today's Sales in Toman + Average Ticket
        item {
            SalesHeroCard(
                salesToman = uiState.todaySalesToman,
                txCount = uiState.todayTransactionsCount
            )
        }

        // 3. Metric Row: Count & Pending Queue
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "تراکنش‌های امروز",
                    value = PersianNumberFormatter.toPersianDigits(uiState.todayTransactionsCount.toString()),
                    unit = "تراکنش",
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    iconTint = TealAccent,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "در صف ارسال",
                    value = PersianNumberFormatter.toPersianDigits(uiState.pendingQueueCount.toString()),
                    unit = "پیامک",
                    icon = Icons.AutoMirrored.Filled.ScheduleSend,
                    iconTint = AmberGold,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Section Header: Recent Transactions & "مشاهده همه"
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "آخرین تراکنش‌ها",
                    style = CockpitTypography.titleMedium,
                    color = TextPrimary
                )
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateToTransactions() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "مشاهده همه",
                        style = CockpitTypography.bodyMedium,
                        color = TealPrimaryLight
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "مشاهده همه",
                        tint = TealPrimaryLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 5. Recent Transactions List or Guidance Empty State
        if (uiState.recentTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SurfaceElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "هنوز تراکنشی برای امروز ثبت نشده است",
                            style = CockpitTypography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "به محض دریافت پیامک واریز بانکی، تراکنش‌ها به صورت لحظه‌ای در این بخش ظاهر می‌شوند.",
                            style = CockpitTypography.bodySmall,
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(uiState.recentTransactions) { tx ->
                RecentTransactionRow(
                    transaction = tx,
                    onClick = { onNavigateToDetail(tx.messageId) }
                )
            }
        }
    }
}

@Composable
fun SalesHeroCard(
    salesToman: Long,
    txCount: Int
) {
    val avgTicketToman = if (txCount > 0) salesToman / max(1, txCount) else 0L

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Navy800,
                            Color(0xFF132F3D),
                            Navy800
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                // Header: Badge + Label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = TealAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "فروش امروز",
                            style = CockpitTypography.titleSmall,
                            color = TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x2214B8A6))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "زنده و خودکار",
                            style = CockpitTypography.labelSmall,
                            color = TealAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Amount in Toman
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = PersianNumberFormatter.formatNumber(salesToman),
                        style = CockpitTypography.headlineLarge.copy(fontSize = 32.sp),
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "تومان",
                        style = CurrencyUnitStyle.copy(fontSize = 16.sp),
                        color = TealAccent,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // Equivalent in Rial
                Text(
                    text = "معادل: ${PersianNumberFormatter.formatRial(salesToman * 10)}",
                    style = CockpitTypography.bodySmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Insight Pill: Average ticket
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x330A0F1D))
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = TealPrimaryLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "میانگین هر تراکنش:",
                            style = CockpitTypography.bodySmall,
                            color = TextSecondary
                        )
                        Text(
                            text = "${PersianNumberFormatter.formatNumber(avgTicketToman)} تومان",
                            style = CockpitTypography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = CockpitTypography.titleSmall,
                    color = TextSecondary
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    style = CockpitTypography.titleLarge.copy(fontSize = 22.sp),
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = unit,
                    style = CockpitTypography.bodySmall,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}

@Composable
fun RecentTransactionRow(
    transaction: TransactionEntity,
    onClick: () -> Unit
) {
    val bankInfo = getBankInfo(transaction.bankId)
    val cardDisplay = transaction.cardLast4?.let { "کارت: •••• ${PersianNumberFormatter.toPersianDigits(it)}" } ?: "کارت نامشخص"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BankAvatar(bankId = transaction.bankId, size = 42.dp)
                Column {
                    Text(
                        text = bankInfo.nameFa,
                        style = CockpitTypography.titleMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = cardDisplay,
                        style = CockpitTypography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+ ${PersianNumberFormatter.formatToman(transaction.amountRial / 10)}",
                    style = CockpitTypography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TealAccent
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusPill(status = transaction.status)
            }
        }
    }
}

