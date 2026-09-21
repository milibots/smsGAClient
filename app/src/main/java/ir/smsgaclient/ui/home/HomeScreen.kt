// app/src/main/java/ir/smsgaclient/ui/home/HomeScreen.kt
package ir.smsgaclient.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import ir.smsgaclient.data.db.entity.TransactionEntity
import ir.smsgaclient.ui.common.BridgeStatusBar
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.Navy700
import ir.smsgaclient.ui.theme.SurfaceDark
import ir.smsgaclient.ui.theme.TealPrimary
import ir.smsgaclient.ui.theme.TealPrimaryLight
import ir.smsgaclient.ui.theme.TextMuted
import ir.smsgaclient.ui.theme.TextPrimary
import ir.smsgaclient.ui.theme.TextSecondary
import ir.smsgaclient.util.PersianNumberFormatter

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
            .background(SurfaceDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Bridge Status Indicator Bar
        item {
            BridgeStatusBar(status = uiState.bridgeStatus)
        }

        // 2. Hero Card: Today's Sales in Toman
        item {
            SalesHeroCard(salesToman = uiState.todaySalesToman)
        }

        // 3. Metric Row: Count & Queue
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "تراکنش‌های امروز",
                    value = PersianNumberFormatter.toPersianDigits(uiState.todayTransactionsCount.toString()),
                    unit = "تراکنش",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "در صف ارسال",
                    value = PersianNumberFormatter.toPersianDigits(uiState.pendingQueueCount.toString()),
                    unit = "پیامک",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Section Header: Last Transactions & "مشاهده همه"
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
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "مشاهده همه",
                        tint = TealPrimaryLight
                    )
                }
            }
        }

        // 5. Recent 3 Transactions List
        if (uiState.recentTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Text(
                        text = "هنوز تراکنشی برای امروز ثبت نشده است",
                        style = CockpitTypography.bodyMedium,
                        color = TextMuted,
                        modifier = Modifier.padding(24.dp)
                    )
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
fun SalesHeroCard(salesToman: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "فروش امروز",
                style = CockpitTypography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = PersianNumberFormatter.formatNumber(salesToman),
                style = CockpitTypography.headlineLarge,
                color = TextPrimary
            )
            Text(
                text = "تومان",
                style = CockpitTypography.titleMedium,
                color = TealPrimary
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = CockpitTypography.bodySmall ?: CockpitTypography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    style = CockpitTypography.titleLarge,
                    color = TextPrimary
                )
                Text(
                    text = unit,
                    style = CockpitTypography.bodySmall ?: CockpitTypography.bodyMedium,
                    color = TextMuted
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "بانک ${transaction.bankId}",
                    style = CockpitTypography.titleMedium,
                    color = TextPrimary
                )
                val cardDisplay = transaction.cardLast4?.let { "کارت: ****$it" } ?: ""
                Text(
                    text = cardDisplay,
                    style = CockpitTypography.bodyMedium,
                    color = TextMuted
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = PersianNumberFormatter.formatToman(transaction.amountRial / 10),
                    style = CockpitTypography.titleMedium,
                    color = TealPrimaryLight
                )
                Text(
                    text = transaction.status,
                    style = CockpitTypography.bodySmall ?: CockpitTypography.bodyMedium,
                    color = TextMuted
                )
            }
        }
    }
}
