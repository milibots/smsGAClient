package ir.smsgaclient.ui.cards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.smsgaclient.ui.common.EmvChipGraphic
import ir.smsgaclient.ui.common.getBankInfo
import ir.smsgaclient.ui.theme.BackgroundMidnight
import ir.smsgaclient.ui.theme.BorderSubtle
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.CardNumberStyle
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.PureBlack
import ir.smsgaclient.ui.theme.PureWhite
import ir.smsgaclient.ui.theme.SilverPlatinum
import ir.smsgaclient.ui.theme.SurfaceElevated
import ir.smsgaclient.ui.theme.TextMuted
import ir.smsgaclient.ui.theme.TextPrimary
import ir.smsgaclient.ui.theme.TextSecondary
import ir.smsgaclient.util.PersianNumberFormatter
import kotlin.math.max

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
                containerColor = PureWhite,
                contentColor = PureBlack,
                shape = CircleShape,
                modifier = Modifier.size(58.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "افزودن کارت",
                    tint = PureBlack
                )
            }
        },
        containerColor = BackgroundMidnight,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "کارت‌های بانکی و سقف روزانه",
                        style = CockpitTypography.headlineMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "پایش هوشمند سقف انتقال وجه و پیشگیری از مسدودی",
                        style = CockpitTypography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.cards.isEmpty()) {
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
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(SurfaceElevated)
                                .border(1.dp, BorderSubtle, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "هنوز کارتی ثبت نشده است",
                            style = CockpitTypography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "با افزودن کارت‌های بانکی مقصد، مصرف روزانه و سقف هر کارت به صورت زنده رصد می‌شود.",
                            style = CockpitTypography.bodySmall,
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(22.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PureWhite,
                                contentColor = PureBlack
                            )
                        ) {
                            Text(text = "افزودن اولین کارت", color = PureBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = uiState.cards,
                        key = { it.card.id }
                    ) { cardUsage ->
                        BankCardWithQuotaCard(cardUsage = cardUsage)
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
fun BankCardWithQuotaCard(cardUsage: CardWithUsage) {
    val card = cardUsage.card
    val bankInfo = getBankInfo(card.bankId)
    val remainingRial = max(0L, card.dailyLimitRial - cardUsage.usageRial)
    val remainingToman = remainingRial / 10

    val animatedProgress by animateFloatAsState(
        targetValue = cardUsage.usagePercent.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800),
        label = "quotaProgress"
    )

    val progressColor = when {
        cardUsage.usagePercent >= 1.0f -> PureWhite
        cardUsage.usagePercent >= 0.9f -> SilverPlatinum
        else -> PureWhite
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF282A30),
                                Color(0xFF181A1E),
                                Color(0xFF0C0D10)
                            )
                        )
                    )
                    .border(1.dp, Color(0xFF383842), RoundedCornerShape(22.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.matchParentSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bankInfo.nameFa,
                            style = CockpitTypography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        EmvChipGraphic(modifier = Modifier.size(width = 30.dp, height = 22.dp))
                    }

                    Text(
                        text = "••••  ••••  ••••  ${PersianNumberFormatter.toPersianDigits(card.last4)}",
                        style = CardNumberStyle.copy(fontSize = 17.sp),
                        color = Color.White
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = card.holderName.ifEmpty { "صاحب حساب" },
                            style = CockpitTypography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "شتاب",
                            style = CockpitTypography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "میزان مصرف روزانه",
                    style = CockpitTypography.titleSmall,
                    color = TextSecondary
                )
                Text(
                    text = "${PersianNumberFormatter.toPersianDigits((cardUsage.usagePercent * 100).toInt().toString())}٪",
                    style = CockpitTypography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = progressColor,
                trackColor = SurfaceElevated
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "مصرف شده:",
                        style = CockpitTypography.bodySmall,
                        color = TextMuted
                    )
                    Text(
                        text = PersianNumberFormatter.formatToman(cardUsage.usageRial / 10),
                        style = CockpitTypography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "مانده تا سقف:",
                        style = CockpitTypography.bodySmall,
                        color = TextMuted
                    )
                    Text(
                        text = PersianNumberFormatter.formatToman(remainingToman),
                        style = CockpitTypography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
            }
        }
    }
}

@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onConfirm: (bank: String, last4: String, holder: String, limit: Long) -> Unit
) {
    var selectedBank by remember { mutableStateOf("blu") }
    var last4 by remember { mutableStateOf("") }
    var holder by remember { mutableStateOf("") }
    var limitToman by remember { mutableStateOf("50000000") }

    val popularBanks = listOf(
        "blu" to "بلوبانک",
        "pasargad" to "پاسارگاد",
        "saman" to "سامان",
        "mellat" to "ملت",
        "melli" to "ملی",
        "sepah" to "سپه",
        "keshavarzi" to "کشاورزی"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "افزودن کارت بانکی جدید",
                style = CockpitTypography.titleLarge,
                color = TextPrimary
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "انتخاب بانک:",
                    style = CockpitTypography.titleSmall,
                    color = TextSecondary
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(popularBanks) { (id, name) ->
                        val isSelected = selectedBank == id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedBank = id },
                            shape = RoundedCornerShape(20.dp),
                            label = { Text(text = name, style = CockpitTypography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PureWhite,
                                selectedLabelColor = PureBlack,
                                containerColor = SurfaceElevated,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = last4,
                    onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) last4 = it },
                    label = { Text("۴ رقم آخر کارت") },
                    placeholder = { Text("مثلاً ۴۸۲۱") },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = holder,
                    onValueChange = { holder = it },
                    label = { Text("نام صاحب کارت") },
                    placeholder = { Text("مثلاً علی رضایی") },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = limitToman,
                    onValueChange = { if (it.all { char -> char.isDigit() }) limitToman = it },
                    label = { Text("سقف روزانه (تومان)") },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val limitVal = (limitToman.toLongOrNull() ?: 50_000_000L) * 10
                    if (last4.length == 4) {
                        onConfirm(selectedBank, last4, holder, limitVal)
                    }
                },
                enabled = last4.length == 4,
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PureWhite,
                    contentColor = PureBlack
                )
            ) {
                Text(text = "افزودن کارت", color = PureBlack, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Text(text = "انصراف", color = TextSecondary)
            }
        },
        containerColor = CardBackground,
        shape = RoundedCornerShape(32.dp)
    )
}
