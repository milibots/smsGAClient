# smsGAClient v1.0.1 — Persian Fintech Cockpit & Background SMS Bridge

> **سامانه دریافت و مدیریت کارت‌به‌کارت و بریج ارسال وب‌هوک پیامک‌های بانکی برای پذیرندگان ایرانی**
> Production-grade Kotlin Android application turning any merchant's phone into a reliable card-to-card payment cockpit and zero-loss SMS forwarding bridge.

---

## 🌟 What's New in v1.0.1 / تغییرات نسخه ۱.۰.۱

### ⚡ Background Reliability & Battery Engine (ماندگاری ۲۴ ساعته)
- **Automatic Doze Mode Exemption**: Native prompt (`ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`) exempting the app from Android battery restrictions so bank SMS are forwarded even when the screen is turned off or in deep sleep.
- **Background Permission Warning Banner**: Real-time status banner on Cockpit Home warning the merchant if SMS permissions or battery optimization are disabled.
- **Manufacturer-Specific AutoStart Guidance (OEM)**: Dedicated instructions and one-tap intent triggers for Xiaomi (MIUI/HyperOS Autostart), Samsung (Sleeping apps bypass), Huawei (App Launch manual mode), Oppo, and Vivo.
- **Interactive Battery Settings**: Live status badge in Settings with instant exemption request button.

### 🧹 Streamlined Clean Codebase
- **Zero-Comment Source Code**: Fully stripped internal comments and docstrings for a lean, production-grade clean code standard.
- **Clean Repository Foundation**: Purged all temporary files, agent task queues, and editor scratch files.

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
app-prod-arm64-v8a-release.apk   : A22D4B0E5EB563D38D9F71BE993901D844686389B32BFABF2DADB8F97C663F95
app-prod-armeabi-v7a-release.apk : 389F60031D5C4D9685F3F90385C31C459988EFA58F68C591629DABA7E7A9DAA7
app-prod-universal-release.apk   : A29A608A1CD1643379A48AD30E76928806A706475E985DE3426B1F45471409BF
app-prod-x86-release.apk         : 99B29D728F4BEFAA6C53553077C4672597B2D68DF317BCCACBE07F11C66A376E
app-prod-x86_64-release.apk      : DC1A483E9A175FC997FAC23E1BFA9AB1A93934022833A373F0EBDCBA2D815225
```

---

## 🛡️ Privacy & Compliance
- **Zero Advertising / Zero Analytics SDKs**: No Google Firebase, no third-party trackers, no telemetry.
- **No SMS Send Permission**: The app strictly requires `RECEIVE_SMS` & `READ_SMS` only. It cannot send SMS or incur operator charges.
- **Local SQLite DB with SQLCipher**: All transaction records remain encrypted on the merchant's physical device.
