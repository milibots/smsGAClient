# smsGAClient v1.0.0 — Persian Fintech Cockpit & Background SMS Bridge

> **سامانه دریافت و مدیریت کارت‌به‌کارت و بریج ارسال وب‌هوک پیامک‌های بانکی برای پذیرندگان ایرانی**
> Production-grade Kotlin Android application turning any merchant's phone into a reliable card-to-card payment cockpit and zero-loss SMS forwarding bridge.

---

## 🌟 Highlights / ویژگی‌های برجسته

### 1. The Bridge (موتور انتقال پیامک‌ها بدون قطعی و خطا)
- **Zero SMS Loss Architecture**: BroadcastReceiver high-priority (`999`) + Foreground Service + WorkManager exponential backoff guarantee.
- **Boot Persistence**: Automatic start on device reboot via `RECEIVE_BOOT_COMPLETED`.
- **Battery Optimization Bypass**: Direct merchant guided intent to disable battery optimizations (`REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`).
- **Heartbeat Daemon**: Periodic check-in every 15 minutes reporting battery level, network type, and service health to merchant server.
- **Bank SMS Parser**: High-performance normalized regex parser extracting Amount, Type (Deposit/Withdrawal), Tracking Code, Card Number, and Balance for major Iranian banks:
  - بلوبانک (Blu Bank)
  - بانک سامان (Saman)
  - بانک پاسارگاد (Pasargad)
  - بانک ملت (Mellat)
  - بانک سپه (Sepah)
  - بانک ملی ایران (Melli)
  - بانک صادرات، پارسیان، تجارت، شهر، کشاورزی و آینده
- **HMAC-SHA256 Signatures**: Every forwarded webhook payload is cryptographically signed using a merchant-configured secret key to prevent replay and spoofing attacks.

### 2. The Cockpit (داشبورد مدرن متریال ۳ پذیرنده)
- **Elevated Iranian Fintech Aesthetics**: Powered by `ui-ux-pro-max` & `websima` design system principles.
- **Persian-First & True RTL**: Dedicated typography spacing (1.65–1.8x line-height) preventing Persian diacritic and glyph clipping.
- **OLED Dark Mode & Iranian Bank Palette**: Authentic brand accents for each bank, EMV chip visual simulation, and glowing live bridge status indicator.
- **Card Quota & Turnover Tracking**: Daily deposit limits (1,000,000,000 Rials), live transaction counts, and visual capacity meters.
- **Iranian Bank Receipt Detail**: Authentic thermal receipt aesthetic with instant clipboard copying for tracking numbers and card PANs.
- **Security Vault**: Android Keystore hardware-backed AES256-GCM encryption for webhook URL and HMAC secrets with biometric unlock protection.

---

## 📦 Download APKs / دانلود فایل‌های نصبی

| Architecture / نوع معماری | File Name | Size | Recommended For |
| :--- | :--- | :--- | :--- |
| **ARM 64-bit (Modern)** | `app-prod-arm64-v8a-release.apk` | 1.56 MB | اکثر گوشی‌های مدرن (Samsung, Xiaomi, Pixel) |
| **ARM 32-bit (Legacy)** | `app-prod-armeabi-v7a-release.apk` | 1.56 MB | گوشی‌ها و کارت‌خوان‌های قدیمی اندرویدی |
| **Universal (All-in-One)** | `app-prod-universal-release.apk` | 1.61 MB | سازگار با تمامی گوشی‌ها و پردازنده‌ها |
| **x86 64-bit (PC / Emulator)** | `app-prod-x86_64-release.apk` | 1.56 MB | شبیه‌سازهای کامپیوتر ۶۴ بیتی و ChromeOS |
| **x86 32-bit (PC / Emulator)** | `app-prod-x86-release.apk` | 1.56 MB | شبیه‌سازهای کامپیوتر ۳۲ بیتی |

---

## 🔒 SHA-256 Checksums / اعتبارسنجی فایل‌ها

```text
app-prod-arm64-v8a-release.apk   : 75465A92E13325FF9188257B2E981E84B41DF1E703B431939BA3E65265616F0F
app-prod-armeabi-v7a-release.apk : D9F845555D9DB4C21A202AA27A0348EA21588F889D0AA85E43A820C6D4D79045
app-prod-universal-release.apk   : 5B568B8F6A48244FF31843D7EAE2059C6D9724DC8F037A51F476FFADEA846B79
app-prod-x86-release.apk         : C2AA999CC06316A9CD3155D48939B19EA9C98330FFBB86716548998BD4ED67FD
app-prod-x86_64-release.apk      : CCF5F510F721E86FA5CE338DCAF65622505E5FE91FB7A91B62460120D7AF7585
```

---

## 🛡️ Privacy & Compliance
- **Zero Advertising / Zero Analytics SDKs**: No Google Firebase, no third-party trackers, no telemetry.
- **No SMS Send Permission**: The app strictly requires `RECEIVE_SMS` & `READ_SMS` only. It cannot send SMS or incur operator charges.
- **Local SQLite DB with SQLCipher**: All transaction records remain encrypted on the merchant's physical device.
