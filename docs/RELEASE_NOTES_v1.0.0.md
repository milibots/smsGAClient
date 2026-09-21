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

### 3. Background Reliability & Battery Optimization (ماندگاری ۲۴ ساعته)
- **Automatic Doze Mode Exemption**: Native prompt (`ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`) exempting the app from Android battery restrictions so bank SMS are forwarded even when the screen is turned off or in deep sleep.
- **Background Permission Warning Banner**: Real-time status banner on Cockpit Home warning the merchant if SMS permissions or battery optimization are disabled.
- **Manufacturer-Specific AutoStart Guidance (OEM)**: Dedicated instructions and one-tap intent triggers for Xiaomi (MIUI/HyperOS Autostart), Samsung (Sleeping apps bypass), Huawei (App Launch manual mode), Oppo, and Vivo.
- **Interactive Battery Settings**: Live status badge in Settings with instant exemption request button.

---

## 📦 Download APKs / دانلود فایل‌های نصبی

| Architecture / نوع معماری | File Name | Size | Recommended For |
| :--- | :--- | :--- | :--- |
| **ARM 64-bit (Modern)** | `app-prod-arm64-v8a-release.apk` | 1.57 MB | اکثر گوشی‌های مدرن (Samsung, Xiaomi, Pixel) |
| **ARM 32-bit (Legacy)** | `app-prod-armeabi-v7a-release.apk` | 1.57 MB | گوشی‌ها و کارت‌خوان‌های قدیمی اندرویدی |
| **Universal (All-in-One)** | `app-prod-universal-release.apk` | 1.62 MB | سازگار با تمامی گوشی‌ها و پردازنده‌ها |
| **x86 64-bit (PC / Emulator)** | `app-prod-x86_64-release.apk` | 1.57 MB | شبیه‌سازهای کامپیوتر ۶۴ بیتی و ChromeOS |
| **x86 32-bit (PC / Emulator)** | `app-prod-x86-release.apk` | 1.57 MB | شبیه‌سازهای کامپیوتر ۳۲ بیتی |

---

## 🔒 Signature Verification & SHA-256 Checksums / اعتبارسنجی امضا و هش فایل‌ها

All APK binaries are cryptographically signed with **v1 (JAR)**, **v2 (APK Signature Scheme v2)**, and **v3 (APK Signature Scheme v3)** and verified with `zipalign` 4-byte alignment:
- **Certificate DN**: `CN=smsGAClient, OU=Fintech, O=milibots, L=Tehran, ST=Tehran, C=IR`
- **Certificate SHA-256**: `198cdd70e5c891ca332e815377a469b08e3b20f9d2bb5c5129fa4c0a391ff6d9`

```text
app-prod-arm64-v8a-release.apk   : BC9B6248C11DAE470C0B163C46861A6F76F9D4C7727FDFC687AB22813904661F
app-prod-armeabi-v7a-release.apk : 226BD1AB4E9F709FEFE2C32E1AA8BB4D420B5BCB0331186492CD38D4AA3A4684
app-prod-universal-release.apk   : FA5B99C26AA70807D070A4BE973A8C40BBFA07CE9880AED18B46AAD05BCFC9AE
app-prod-x86-release.apk         : B76C7DDE5A559FB00CDE6659535F8C010E264157463C627CE6B5A63217A7DA61
app-prod-x86_64-release.apk      : 71DFF7D7656A89EBCC43EBA1371E48119911F870BE2193EE17231BE81C0A7309
```

---

## 🛡️ Privacy & Compliance
- **Zero Advertising / Zero Analytics SDKs**: No Google Firebase, no third-party trackers, no telemetry.
- **No SMS Send Permission**: The app strictly requires `RECEIVE_SMS` & `READ_SMS` only. It cannot send SMS or incur operator charges.
- **Local SQLite DB with SQLCipher**: All transaction records remain encrypted on the merchant's physical device.
