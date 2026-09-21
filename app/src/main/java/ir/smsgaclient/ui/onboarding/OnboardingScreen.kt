// app/src/main/java/ir/smsgaclient/ui/onboarding/OnboardingScreen.kt
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.smsgaclient.ui.theme.CardBackground
import ir.smsgaclient.ui.theme.CockpitTypography
import ir.smsgaclient.ui.theme.StatusNotForwarding
import ir.smsgaclient.ui.theme.SurfaceDark
import ir.smsgaclient.ui.theme.TealPrimary
import ir.smsgaclient.ui.theme.TextMuted
import ir.smsgaclient.ui.theme.TextPrimary
import ir.smsgaclient.ui.theme.TextSecondary

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(1) }
    var smsPermissionGranted by remember { mutableStateOf(false) }

    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val receiveGranted = permissions[Manifest.permission.RECEIVE_SMS] == true
        val readGranted = permissions[Manifest.permission.READ_SMS] == true
        smsPermissionGranted = receiveGranted && readGranted
        if (smsPermissionGranted) {
            currentStep = 3 // Next step: Notifications
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        currentStep = 4 // Next step: Battery optimization
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (currentStep) {
                    1 -> {
                        // Step 1: Welcome
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
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { currentStep = 2 },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Text(text = "شروع راه‌اندازی (کمتر از ۲ دقیقه)")
                        }
                    }

                    2 -> {
                        // Step 2: SMS Permissions (Blocking)
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
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                smsPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.RECEIVE_SMS,
                                        Manifest.permission.READ_SMS
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Text(text = "اعطای مجوز پیامک (الزامی)")
                        }
                    }

                    3 -> {
                        // Step 3: Notification Permission (Skippable)
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
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    currentStep = 4
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Text(text = "اعطای مجوز اعلان")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { currentStep = 4 },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "رد کردن این مرحله")
                        }
                    }

                    4 -> {
                        // Step 4: Battery Optimization Exemption
                        Text(
                            text = "معافیت بهینه‌سازی باتری",
                            style = CockpitTypography.headlineMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "جهت تضمین عدم از دست رفتن پیامک‌ها در حالت خاموش بودن صفحه گوشی (Doze mode).",
                            style = CockpitTypography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { currentStep = 5 },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Text(text = "تنظیم بهینه‌سازی باتری")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { currentStep = 5 },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "رد کردن")
                        }
                    }

                    5 -> {
                        // Step 5: Pairing Code
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
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "۴۸۲ - ۱۹۳",
                            style = CockpitTypography.headlineLarge,
                            color = TealPrimary
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onComplete,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Text(text = "ورود به داشبورد")
                        }
                    }
                }
            }
        }
    }
}
