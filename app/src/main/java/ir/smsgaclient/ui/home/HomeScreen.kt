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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import ir.smsgaclient.ui.theme.BackgroundMidnight
import ir.smsgaclient.ui.theme.BorderSubtle
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.CurrencyUnitStyle
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
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToTransactions: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    var hasSmsPermission by remember {
        mutableStateOf(ir.smsgaclient.util.PermissionManager.hasSmsPermissions(context))
    }
    var isBatteryExempt by remember {
        mutableStateOf(ir.smsgaclient.util.PermissionManager.isBatteryOptimizationIgnored(context))
    }
    var showSetupDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                hasSmsPermission = ir.smsgaclient.util.PermissionManager.hasSmsPermissions(context)
                isBatteryExempt = ir.smsgaclient.util.PermissionManager.isBatteryOptimizationIgnored(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val smsPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasSmsPermission = (permissions[android.Manifest.permission.RECEIVE_SMS] == true) &&
                (permissions[android.Manifest.permission.READ_SMS] == true)
        isBatteryExempt = ir.smsgaclient.util.PermissionManager.isBatteryOptimizationIgnored(context)
    }

    if (showSetupDialog) {
        ir.smsgaclient.ui.common.BackgroundSetupModalDialog(
            hasSmsPermission = hasSmsPermission,
            isBatteryExempted = isBatteryExempt,
            onRequestSms = {
                smsPermissionLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.RECEIVE_SMS,
                        android.Manifest.permission.READ_SMS
                    )
                )
            },
            onRequestBatteryExemption = {
                ir.smsgaclient.util.PermissionManager.requestIgnoreBatteryOptimization(context)
                isBatteryExempt = ir.smsgaclient.util.PermissionManager.isBatteryOptimizationIgnored(context)
            },
            onOpenOemSettings = {
                ir.smsgaclient.util.PermissionManager.openOemAutoStartSettings(context)
            },
            onDismiss = {
                showSetupDialog = false
                hasSmsPermission = ir.smsgaclient.util.PermissionManager.hasSmsPermissions(context)
                isBatteryExempt = ir.smsgaclient.util.PermissionManager.isBatteryOptimizationIgnored(context)
            },
            oemGuidance = ir.smsgaclient.util.PermissionManager.getOemGuidanceText()
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundMidnight)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        if (!hasSmsPermission || !isBatteryExempt) {
            item {
                ir.smsgaclient.ui.common.BackgroundPermissionWarningBanner(
                    isSmsMissing = !hasSmsPermission,
                    isBatteryOptimized = !isBatteryExempt,
                    onResolveClick = { showSetupDialog = true }
                )
            }
        }

        item {
            BridgeStatusBar(status = uiState.bridgeStatus)
        }

        item {
            SalesHeroCard(
                salesToman = uiState.todaySalesToman,
                txCount = uiState.todayTransactionsCount
            )
        }

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
                    iconTint = PureWhite,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "در صف ارسال",
                    value = PersianNumberFormatter.toPersianDigits(uiState.pendingQueueCount.toString()),
                    unit = "پیامک",
                    icon = Icons.AutoMirrored.Filled.ScheduleSend,
                    iconTint = SilverPlatinum,
                    modifier = Modifier.weight(1f)
                )
            }
        }

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
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                        .clickable { onNavigateToTransactions() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "مشاهده همه",
                        style = CockpitTypography.labelMedium,
                        color = PureWhite
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "مشاهده همه",
                        tint = PureWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        if (uiState.recentTransactions.isEmpty()) {
            item {
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
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
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
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383840))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF222226),
                            Color(0xFF141416),
                            Color(0xFF0B0B0D)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = PureWhite,
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
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF2C2C32))
                            .border(1.dp, Color(0xFF42424A), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "زنده و خودکار",
                            style = CockpitTypography.labelSmall,
                            color = PureWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = PersianNumberFormatter.formatNumber(salesToman),
                        style = CockpitTypography.headlineLarge.copy(fontSize = 34.sp),
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Text(
                        text = "تومان",
                        style = CurrencyUnitStyle.copy(fontSize = 16.sp),
                        color = SilverPlatinum,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Text(
                    text = "معادل: ${PersianNumberFormatter.formatRial(salesToman * 10)}",
                    style = CockpitTypography.bodySmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF18181C))
                        .border(1.dp, Color(0xFF2E2E34), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(18.dp)
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
                            color = PureWhite
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
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
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
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated)
                        .border(1.dp, BorderSubtle, CircleShape),
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

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = value,
                    style = CockpitTypography.titleLarge.copy(fontSize = 24.sp),
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
                Spacer(modifier = Modifier.height(6.dp))
                StatusPill(status = transaction.status)
            }
        }
    }
}
