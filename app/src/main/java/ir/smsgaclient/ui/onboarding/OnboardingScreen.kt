package ir.smsgaclient.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(1) }
    var smsPermissionGranted by remember { mutableStateOf(false) }

    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val receiveGranted = permissions[Manifest.permission.RECEIVE_SMS] == true
        val readGranted = permissions[Manifest.permission.READ_SMS] == true
        smsPermissionGranted = receiveGranted && readGranted
        if (smsPermissionGranted) {
            currentStep = 3
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        currentStep = 4
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                if (currentStep == 2 && ir.smsgaclient.util.PermissionManager.hasSmsPermissions(context)) {
                    smsPermissionGranted = true
                    currentStep = 3
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundMidnight)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(34.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
        ) {
            Column(
                modifier = Modifier.padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (currentStep) {
                    1 -> {
                        ir.smsgaclient.ui.common.MonochromeSmsGaLogo(size = 88.dp)
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "به smsGA خوش آمدید",
                            style = CockpitTypography.headlineLarge,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "تبدیل گوشی اندروید به داشبورد و درگاه پرداخت کارت به کارت متصل به وب‌سایت فروشگاهی شما.",
                            style = CockpitTypography.bodyLarge,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                        Button(
                            onClick = { currentStep = 2 },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PureWhite,
                                contentColor = PureBlack
                            )
                        ) {
                            Text(
                                text = "شروع راه‌اندازی (کمتر از ۲ دقیقه)",
                                color = PureBlack,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    2 -> {
                        Text(
                            text = "مجوز دسترسی به پیامک",
                            style = CockpitTypography.headlineMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "برای شناسایی و پردازش خودکار واریزی‌های بانکی، مجوز دریافت و خواندن پیامک اجباری است. هیچ پیامکی به سرور شخص ثالث ارسال نمی‌شود.",
                            style = CockpitTypography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                        Button(
                            onClick = {
                                smsPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.RECEIVE_SMS,
                                        Manifest.permission.READ_SMS
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PureWhite,
                                contentColor = PureBlack
                            )
                        ) {
                            Text(
                                text = "اعطای مجوز پیامک (الزامی)",
                                color = PureBlack,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                ir.smsgaclient.util.PermissionManager.openAppSettings(context)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Text(
                                text = "اگر پنجره باز نشد: باز کردن تنظیمات گوشی",
                                style = CockpitTypography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    3 -> {
                        Text(
                            text = "مجوز اعلان‌ها",
                            style = CockpitTypography.headlineMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "برای نمایش وضعیت واریزها و هشدارهای سقف کارت به مجوز ارسال اعلان نیاز است.",
                            style = CockpitTypography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    currentStep = 4
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PureWhite,
                                contentColor = PureBlack
                            )
                        ) {
                            Text(
                                text = "اعطای مجوز اعلان",
                                color = PureBlack,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { currentStep = 4 },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Text(text = "رد کردن این مرحله", color = TextSecondary)
                        }
                    }

                    4 -> {
                        val isBatteryExempt = remember(currentStep) {
                            ir.smsgaclient.util.PermissionManager.isBatteryOptimizationIgnored(context)
                        }

                        Text(
                            text = "معافیت بهینه‌سازی باتری",
                            style = CockpitTypography.headlineMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "سیستم‌عامل اندروید برای صرفه‌جویی در مصرف باتری، برنامه‌های پس‌زمینه را در زمان خاموش بودن صفحه متوقف می‌کند (Doze Mode). جهت تضمین عدم قطعی فوروارد پیامک‌ها، این برنامه باید از بهینه‌سازی باتری مستثنی شود.",
                            style = CockpitTypography.bodyMedium,
                            color = TextSecondary,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        if (isBatteryExempt) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = PureWhite
                                    )
                                    Text(
                                        text = "بهینه‌سازی باتری با موفقیت غیرفعال شد ✓",
                                        style = CockpitTypography.titleSmall,
                                        color = PureWhite
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { currentStep = 5 },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(26.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PureWhite,
                                    contentColor = PureBlack
                                )
                            ) {
                                Text(text = "ادامه به مرحله بعد", color = PureBlack, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = {
                                    ir.smsgaclient.util.PermissionManager.requestIgnoreBatteryOptimization(context)
                                    currentStep = 5
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(26.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PureWhite,
                                    contentColor = PureBlack
                                )
                            ) {
                                Text(
                                    text = "درخواست فعالیت نامحدود باتری",
                                    color = PureBlack,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = { currentStep = 5 },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(26.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Text(text = "رد کردن (توصیه نمی‌شود)", color = TextSecondary)
                            }
                        }
                    }

                    5 -> {
                        Text(
                            text = "جفت‌سازی با پنل مرچنت",
                            style = CockpitTypography.headlineMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "کد ۶ رقمی زیر را در بخش تنظیمات درگاه وارد کنید:",
                            style = CockpitTypography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "۴۸۲ - ۱۹۳",
                            style = CockpitTypography.headlineLarge.copy(fontSize = 36.sp),
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                        Button(
                            onClick = onComplete,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PureWhite,
                                contentColor = PureBlack
                            )
                        ) {
                            Text(text = "ورود به داشبورد", color = PureBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
