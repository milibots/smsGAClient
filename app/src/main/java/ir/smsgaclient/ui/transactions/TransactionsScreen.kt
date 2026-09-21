// app/src/main/java/ir/smsgaclient/ui/transactions/TransactionsScreen.kt
package ir.smsgaclient.ui.transactions

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.smsgaclient.data.db.entity.TransactionEntity
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.Navy700
import ir.smsgaclient.ui.theme.Navy800
import ir.smsgaclient.ui.theme.SurfaceDark
import ir.smsgaclient.ui.theme.TealPrimary
import ir.smsgaclient.ui.theme.TealPrimaryLight
import ir.smsgaclient.ui.theme.TextMuted
import ir.smsgaclient.ui.theme.TextPrimary
import ir.smsgaclient.util.PersianNumberFormatter

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(16.dp)
    ) {
        // 1. Header Title
        Text(
            text = "تراکنش‌ها",
            style = CockpitTypography.headlineMedium,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Search Input
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = { Text(text = "جستجو بر اساس بانک، کارت یا مبلغ...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "جستجو")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardBackground,
                unfocusedContainerColor = CardBackground,
                focusedBorderColor = TealPrimary,
                unfocusedBorderColor = Navy700,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Date Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(DateFilter.values()) { filter ->
                val isSelected = uiState.selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.onFilterSelected(filter) },
                    label = { Text(text = filter.titleFa) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealPrimary,
                        selectedLabelColor = TextPrimary,
                        containerColor = Navy800,
                        labelColor = TextMuted
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Transactions List
        if (uiState.filteredTransactions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Text(
                    text = "هیچ تراکنشی یافت نشد",
                    style = CockpitTypography.bodyMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.filteredTransactions) { tx ->
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
fun TransactionItemRow(
    transaction: TransactionEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
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
