package ir.smsgaclient.ui.cards

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.smsgaclient.ui.common.IranianBankCard
import ir.smsgaclient.ui.common.getBankInfo
import ir.smsgaclient.ui.theme.BackgroundMidnight
import ir.smsgaclient.ui.theme.BorderSubtle
import ir.smsgaclient.ui.theme.CardBackground
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
import kotlinx.coroutines.launch

@Composable
fun CardsScreen(
    viewModel: CardsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    showAddSheet = true
                },
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
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                showAddSheet = true
                            },
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
                val pagerState = rememberPagerState(pageCount = { uiState.cards.size })

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            HorizontalPager(
                                state = pagerState,
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                pageSpacing = 12.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) { page ->
                                val cardUsage = uiState.cards[page]
                                IranianBankCard(
                                    bankId = cardUsage.card.bankId,
                                    cardLast4 = cardUsage.card.last4,
                                    holderName = cardUsage.card.holderName
                                )
                            }

                            if (uiState.cards.size > 1) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    repeat(uiState.cards.size) { index ->
                                        val isCurrent = pagerState.currentPage == index
                                        val dotWidth by animateDpAsState(
                                            targetValue = if (isCurrent) 22.dp else 7.dp,
                                            animationSpec = spring(),
                                            label = "carouselDotWidth"
                                        )
                                        Box(
                                            modifier = Modifier
                                                .height(6.dp)
                                                .width(dotWidth)
                                                .clip(CircleShape)
                                                .background(if (isCurrent) PureWhite else SurfaceElevated)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "مصرف روزانه و سقف کارت‌ها",
                            style = CockpitTypography.titleMedium,
                            color = TextPrimary
                        )
                    }

                    items(
                        items = uiState.cards,
                        key = { it.card.id }
                    ) { cardUsage ->
                        BankCardQuotaDetailCard(cardUsage = cardUsage)
                    }
                }
            }
        }

        if (showAddSheet) {
            AddCardBottomSheet(
                onDismiss = { showAddSheet = false },
                onConfirm = { bank, last4, holder, limit ->
                    viewModel.addCard(bank, last4, holder, limit)
                    showAddSheet = false
                }
            )
        }
    }
}

@Composable
fun BankCardQuotaDetailCard(cardUsage: CardWithUsage) {
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
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        if (bankInfo.logoRes != null) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = bankInfo.logoRes),
                                contentDescription = bankInfo.nameFa,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = bankInfo.nameFa,
                            style = CockpitTypography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "•••• ${PersianNumberFormatter.toPersianDigits(card.last4)}",
                            style = CockpitTypography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                Text(
                    text = "${PersianNumberFormatter.toPersianDigits((cardUsage.usagePercent * 100).toInt().toString())}٪",
                    style = CockpitTypography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = progressColor,
                trackColor = SurfaceElevated
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "مصرف شده امروز:",
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
                        text = "مانده تا سقف مجاز:",
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
fun AddCardBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (bank: String, last4: String, holder: String, limit: Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var selectedBank by remember { mutableStateOf("blu") }
    var last4 by remember { mutableStateOf("") }
    var holder by remember { mutableStateOf("") }
    var limitToman by remember { mutableStateOf("50000000") }
    val haptic = LocalHapticFeedback.current

    val popularBanks = listOf(
        "blu" to "بلوبانک",
        "pasargad" to "پاسارگاد",
        "saman" to "سامان",
        "mellat" to "ملت",
        "melli" to "ملی",
        "sepah" to "سپه",
        "keshavarzi" to "کشاورزی"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardBackground,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderSubtle)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "افزودن کارت بانکی جدید",
                style = CockpitTypography.titleLarge,
                color = TextPrimary
            )

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
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedBank = id
                        },
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

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    val limitVal = (limitToman.toLongOrNull() ?: 50_000_000L) * 10
                    if (last4.length == 4) {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            onConfirm(selectedBank, last4, holder, limitVal)
                        }
                    }
                },
                enabled = last4.length == 4,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PureWhite,
                    contentColor = PureBlack
                )
            ) {
                Text(text = "افزودن کارت", color = PureBlack, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Text(text = "انصراف", color = TextSecondary)
            }
        }
    }
}
