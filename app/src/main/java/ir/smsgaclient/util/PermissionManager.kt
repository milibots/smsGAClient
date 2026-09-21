package ir.smsgaclient.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import timber.log.Timber

object PermissionManager {

    fun hasSmsPermissions(context: Context): Boolean {
        val receiveGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED

        val readGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED

        return receiveGranted && readGranted
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun isBatteryOptimizationIgnored(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            ?: return false
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    fun isReadyForBackgroundWork(context: Context): Boolean {
        return hasSmsPermissions(context) && isBatteryOptimizationIgnored(context)
    }

    @SuppressLint("BatteryLife")
    fun requestIgnoreBatteryOptimization(context: Context) {
        if (isBatteryOptimizationIgnored(context)) return

        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to launch direct battery optimization intent, falling back to general list")
            try {
                val fallbackIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (fallbackEx: Exception) {
                Timber.e(fallbackEx, "Failed to launch general battery optimization list")
                openAppSettings(context)
            }
        }
    }

    fun openAppSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to open application details settings")
        }
    }

    fun openOemAutoStartSettings(context: Context): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val oemIntents = mutableListOf<Intent>()

        when {
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || manufacturer.contains("poco") -> {
                oemIntents.add(
                    Intent().apply {
                        component = ComponentName(
                            "com.miui.securitycenter",
                            "com.miui.permcenter.autostart.AutoStartManagementActivity"
                        )
                    }
                )
                oemIntents.add(
                    Intent("miui.intent.action.OP_AUTO_START").apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                    }
                )
            }
            manufacturer.contains("huawei") || manufacturer.contains("honor") -> {
                oemIntents.add(
                    Intent().apply {
                        component = ComponentName(
                            "com.huawei.systemmanager",
                            "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                        )
                    }
                )
                oemIntents.add(
                    Intent().apply {
                        component = ComponentName(
                            "com.huawei.systemmanager",
                            "com.huawei.systemmanager.optimize.process.ProtectActivity"
                        )
                    }
                )
            }
            manufacturer.contains("samsung") -> {
                oemIntents.add(
                    Intent().apply {
                        component = ComponentName(
                            "com.samsung.android.lool",
                            "com.samsung.android.sm.battery.ui.BatteryActivity"
                        )
                    }
                )
            }
            manufacturer.contains("oppo") || manufacturer.contains("realme") -> {
                oemIntents.add(
                    Intent().apply {
                        component = ComponentName(
                            "com.coloros.safecenter",
                            "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                        )
                    }
                )
            }
            manufacturer.contains("vivo") -> {
                oemIntents.add(
                    Intent().apply {
                        component = ComponentName(
                            "com.vivo.permissionmanager",
                            "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                        )
                    }
                )
            }
        }

        for (intent in oemIntents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return true
            } catch (ignored: Exception) {

            }
        }

        openAppSettings(context)
        return false
    }

    fun getOemGuidanceText(): String {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return when {
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || manufacturer.contains("poco") ->
                "در گوشی‌های شیائومی، حتماً گزینه «شروع خودکار (Autostart)» را فعال کرده و تنظیمات باتری را روی «بدون محدودیت (No restrictions)» قرار دهید تا پیامک‌ها در خواب متوقف نشوند."
            manufacturer.contains("samsung") ->
                "در گوشی‌های سامسونگ، بررسی کنید که برنامه در لیست «برنامه‌های به خواب رفته (Sleeping apps)» قرار نداشته باشد و مصرف باتری روی «نامحدود (Unrestricted)» تنظیم باشد."
            manufacturer.contains("huawei") || manufacturer.contains("honor") ->
                "در گوشی‌های هواوی، در بخش مدیریت برنامه (App Launch)، حالت را از اتوماتیک به «مدیریت دستی (Manage manually)» تغییر داده و هر ۳ گزینه شروع خودکار، شروع ثانویه و اجرا در پس‌زمینه را فعال کنید."
            else ->
                "جهت اطمینان از عملکرد مداوم، بهینه‌سازی باتری را برای این برنامه غیرفعال کنید تا هنگام خاموش بودن نمایشگر، ارتباط قطع نشود."
        }
    }
}
