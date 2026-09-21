package ir.smsgaclient.ui.patterns

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.smsgaclient.domain.model.SmsType
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
fun PatternStudioScreen(
    viewModel: PatternStudioViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val popularBanks = listOf(
        "blu" to "بلو",
        "mellat" to "ملت",
        "saman" to "سامان",
        "pasargad" to "پاسارگاد",
        "saderat" to "صادرات",
        "melli" to "ملی",
        "sepah" to "سپه",
        "tejarat" to "تجارت",
        "shahr" to "شهر",
        "custom" to "سایر / دلخواه"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundMidnight)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(SurfaceElevated)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "بازگشت",
                    tint = PureWhite
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "استودیوی هوشمند الگو",
                        style = CockpitTypography.titleLarge,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF26262B))
                            .border(1.dp, Color(0xFF4A4A5A), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Smart AI",
                                style = CockpitTypography.labelSmall,
                                color = PureWhite,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                Text(
                    text = "آموزش و ثبت الگوهای جدید پیامک بانکی بدون نیاز به کدنویسی",
                    style = CockpitTypography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = BorderStroke(1.dp, BorderSubtle)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "۱. نمونه متن پیامک بانک",
                        style = CockpitTypography.titleSmall,
                        color = PureWhite
                    )

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = clipboard.primaryClip
                            if (clip != null && clip.itemCount > 0) {
                                val text = clip.getItemAt(0).text?.toString() ?: ""
                                viewModel.onRawSmsChanged(text)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "چسباندن متن",
                            style = CockpitTypography.labelSmall,
                            color = PureWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.rawSms,
                    onValueChange = { viewModel.onRawSmsChanged(it) },
                    placeholder = { Text("متن پیامک بانکی را اینجا پیست کنید...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                if (state.candidates != null && state.candidates!!.suggestedAmounts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "مبالغ تشخیص‌داده‌شده در متن (لمس کنید):",
                        style = CockpitTypography.labelSmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.candidates!!.suggestedAmounts) { amount ->
                            FilterChip(
                                selected = state.amountRialStr == amount.toString(),
                                onClick = { viewModel.onAmountChanged(amount.toString()) },
                                label = {
                                    Text(
                                        text = "${PersianNumberFormatter.formatNumber(amount)} ریال",
                                        style = CockpitTypography.labelSmall
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PureWhite,
                                    selectedLabelColor = PureBlack,
                                    containerColor = SurfaceElevated,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = BorderStroke(1.dp, BorderSubtle)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "۲. انتخاب یا تعیین بانک",
                    style = CockpitTypography.titleSmall,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(popularBanks) { (id, name) ->
                        val isSelected = state.bankId == id
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onBankSelected(id, name) },
                            shape = RoundedCornerShape(18.dp),
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

                if (state.bankId == "custom") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = state.bankId,
                            onValueChange = { viewModel.onCustomBankChanged(it, state.bankNameFa) },
                            label = { Text("شناسه انگلیسی") },
                            placeholder = { Text("blu / custom") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = state.bankNameFa,
                            onValueChange = { viewModel.onCustomBankChanged(state.bankId, it) },
                            label = { Text("نام فارسی") },
                            placeholder = { Text("بانک سپهر") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "نوع تراکنش:",
                    style = CockpitTypography.titleSmall,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = state.transactionType == SmsType.DEPOSIT,
                        onClick = { viewModel.onTransactionTypeChanged(SmsType.DEPOSIT) },
                        shape = RoundedCornerShape(18.dp),
                        label = { Text(text = "واریز (Deposit)", style = CockpitTypography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PureWhite,
                            selectedLabelColor = PureBlack,
                            containerColor = SurfaceElevated,
                            labelColor = TextSecondary
                        )
                    )

                    FilterChip(
                        selected = state.transactionType == SmsType.WITHDRAW,
                        onClick = { viewModel.onTransactionTypeChanged(SmsType.WITHDRAW) },
                        shape = RoundedCornerShape(18.dp),
                        label = { Text(text = "برداشت (Withdraw)", style = CockpitTypography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PureWhite,
                            selectedLabelColor = PureBlack,
                            containerColor = SurfaceElevated,
                            labelColor = TextSecondary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = state.amountRialStr,
                    onValueChange = { viewModel.onAmountChanged(it) },
                    label = { Text("مبلغ به ریال (الزامی)") },
                    placeholder = { Text("مثلاً ۴۵۰۰۲۸۰") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderSubtle
                    )
                )

                if (state.amountRialStr.toLongOrNull() != null) {
                    val toman = state.amountRialStr.toLong() / 10
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "معادل: ${PersianNumberFormatter.formatNumber(toman)} تومان",
                        style = CockpitTypography.labelSmall,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = state.cardLast4Str,
                        onValueChange = { viewModel.onCardLast4Changed(it) },
                        label = { Text("۴ رقم آخر کارت") },
                        placeholder = { Text("۴۸۲۱") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.balanceRialStr,
                        onValueChange = { viewModel.onBalanceChanged(it) },
                        label = { Text("مانده به ریال") },
                        placeholder = { Text("اختیاری") },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.inferenceResult != null) {
            val res = state.inferenceResult!!
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (res.isValid) Color(0xFF141A16) else Color(0xFF221616)
                ),
                border = BorderStroke(
                    1.dp,
                    if (res.isValid) Color(0xFF2E5A36) else Color(0xFF5A2E2E)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (res.isValid) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = if (res.isValid) Color(0xFF4CAF50) else Color(0xFFFF5252),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (res.isValid) "الگو با موفقیت اعتبارسنجی شد ✓" else "خطا در استنتاج الگو",
                            style = CockpitTypography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (res.isValid) Color(0xFF4CAF50) else Color(0xFFFF5252)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (res.isValid && res.parsedSms != null) {
                        val parsed = res.parsedSms!!
                        Text(
                            text = "بانک: ${res.pattern.nameFa} (${parsed.bankId})  ·  نوع: ${parsed.type}",
                            style = CockpitTypography.bodyMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "مبلغ استخراجی: ${PersianNumberFormatter.formatNumber(parsed.amountToman ?: 0)} تومان (${PersianNumberFormatter.formatNumber(parsed.amountRial ?: 0)} ریال)",
                            style = CockpitTypography.bodyMedium,
                            color = TextPrimary
                        )
                        if (!parsed.cardLast4.isNullOrBlank()) {
                            Text(
                                text = "کارت: •••• ${parsed.cardLast4}",
                                style = CockpitTypography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    } else if (res.error != null) {
                        Text(
                            text = res.error!!,
                            style = CockpitTypography.bodySmall,
                            color = Color(0xFFFF8A80)
                        )
                    }
                }
            }
        }

        if (state.saveSuccessMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2E1E)),
                border = BorderStroke(1.dp, Color(0xFF388E3C))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = state.saveSuccessMessage!!,
                        style = CockpitTypography.bodySmall,
                        color = Color(0xFFE8F5E9)
                    )
                }
            }
        }

        if (state.errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = state.errorMessage!!,
                style = CockpitTypography.bodySmall,
                color = Color(0xFFFF5252)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { viewModel.savePatternToApp() },
            enabled = state.inferenceResult?.isValid == true && !state.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PureWhite,
                contentColor = PureBlack,
                disabledContainerColor = SurfaceElevated,
                disabledContentColor = TextMuted
            )
        ) {
            Text(
                text = if (state.isSaving) "در حال ذخیره‌سازی..." else "ذخیره در برنامه و فعال‌سازی آنی",
                color = if (state.inferenceResult?.isValid == true) PureBlack else TextMuted,
                fontWeight = FontWeight.Bold,
                style = CockpitTypography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                val jsonStr = viewModel.getPatternJson()
                if (jsonStr != null) {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("smsGA Bank Pattern", jsonStr))
                    Toast.makeText(context, "کد JSON الگو در کلیپ‌بورد کپی شد", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "ابتدا پیامک را تنظیم کنید تا الگو تولید شود", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = state.inferenceResult?.isValid == true,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, BorderSubtle)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = null,
                tint = if (state.inferenceResult?.isValid == true) PureWhite else TextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "کپی کد JSON الگو (برای گیت‌هاب / سرور)",
                style = CockpitTypography.labelMedium,
                color = if (state.inferenceResult?.isValid == true) PureWhite else TextMuted
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
