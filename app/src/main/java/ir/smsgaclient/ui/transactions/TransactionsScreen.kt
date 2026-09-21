package ir.smsgaclient.ui.transactions

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ir.smsgaclient.data.db.entity.TransactionEntity
import ir.smsgaclient.ui.common.BankAvatar
import ir.smsgaclient.ui.common.StatusPill
import ir.smsgaclient.ui.common.getBankInfo
import ir.smsgaclient.ui.theme.BackgroundMidnight
import ir.smsgaclient.ui.theme.BorderSubtle
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.PureBlack
import ir.smsgaclient.ui.theme.PureWhite
import ir.smsgaclient.ui.theme.SurfaceElevated
import ir.smsgaclient.ui.theme.TextMuted
import ir.smsgaclient.ui.theme.TextPrimary
import ir.smsgaclient.ui.theme.TextSecondary
import ir.smsgaclient.util.PersianNumberFormatter

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundMidnight)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "تراکنش‌ها",
                style = CockpitTypography.headlineMedium,
                color = TextPrimary
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceElevated)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${PersianNumberFormatter.toPersianDigits(uiState.filteredTransactions.size.toString())} تراکنش",
                    style = CockpitTypography.labelMedium,
                    color = PureWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = { Text(text = "جستجو بر اساس بانک، کارت یا مبلغ...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "جستجو",
                    tint = TextMuted
                )
            },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.onSearchQueryChanged("")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "پاک کردن",
                            tint = TextMuted
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardBackground,
                unfocusedContainerColor = CardBackground,
                focusedBorderColor = PureWhite,
                unfocusedBorderColor = BorderSubtle,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        DateFilterSegmentedBar(
            selectedFilter = uiState.selectedFilter,
            onFilterSelected = { viewModel.onFilterSelected(it) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (uiState.filteredTransactions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
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
                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "تراکنشی مطابق با فیلتر یافت نشد",
                        style = CockpitTypography.titleMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "عبارت جستجو یا فیلتر زمانی انتخابی را تغییر دهید.",
                        style = CockpitTypography.bodySmall,
                        color = TextMuted
                    )
                    if (uiState.searchQuery.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.onSearchQueryChanged("")
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PureWhite,
                                contentColor = PureBlack
                            )
                        ) {
                            Text(text = "پاک کردن جستجو", color = PureBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = uiState.filteredTransactions,
                    key = { it.messageId }
                ) { tx ->
                    TransactionItemRow(
                        transaction = tx,
                        onClick = { onNavigateToDetail(tx.messageId) }
                    )
                }
            }
        }
    }
}

@Composable
fun DateFilterSegmentedBar(
    selectedFilter: DateFilter,
    onFilterSelected: (DateFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        color = SurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DateFilter.values().forEach { filter ->
                val isSelected = selectedFilter == filter
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) PureWhite else Color.Transparent,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "filterBg"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) PureBlack else TextSecondary,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "filterText"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(bgColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onFilterSelected(filter)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter.titleFa,
                        style = CockpitTypography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItemRow(
    transaction: TransactionEntity,
    onClick: () -> Unit
) {
    val bankInfo = getBankInfo(transaction.bankId)
    val cardDisplay = transaction.cardLast4?.let { "کارت: •••• ${PersianNumberFormatter.toPersianDigits(it)}" } ?: "کارت نامشخص"
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BankAvatar(bankId = transaction.bankId, size = 44.dp)
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
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusPill(status = transaction.status)
            }
        }
    }
}
