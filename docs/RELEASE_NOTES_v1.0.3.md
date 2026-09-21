# smsGAClient v1.0.3 — Vazirmatn Persian Typography & Material 3 Bottom Sheets

> **سامانه هوشمند دریافت و مدیریت کارت‌به‌کارت و بریج ارسال پیامک‌های بانکی برای پذیرندگان ایرانی**
> Production-grade Kotlin Android application with authentic Persian typography, Material 3 bottom sheets, and zero-loss SMS forwarding bridge.

---

## 🌟 What's New in v1.0.3 / تغییرات نسخه ۱.۰.۳

### 🖋️ Authentic Persian Typography (تایپوگرافی اصیل فونت وزیرمتن)
- **Vazirmatn Font Family Integration**: Bundled the renowned `Vazirmatn` font family natively across 5 distinct weights (`Light`, `Regular`, `Medium`, `SemiBold`, `Bold`) directly into `res/font/`.
- **Perfect Persian Rendering**: Completely resolved any font clipping or fallback issues on Persian diacritics (گ، ژ، پ، چ، ی) across cards, numbers, receipts, and headers.
- **Consistent Visual Hierarchy**: Applied harmonious font sizing and line heights calibrated specifically for Persian numbers and RTL text reading.

### 📱 Fluid Material 3 Modal Bottom Sheets (ورود باتم‌شیت‌های مدرن)
- **Cards Cockpit ("Add Card")**: Upgraded the add card modal to a fluid bottom sheet with high-radius corners (`32dp`), drag handle, bank chips selector, and tactile haptic feedback.
- **Settings ("Revoke Pairing")**: Replaced system dialogs with a bottom sheet featuring an elevated warning badge and animated coroutine dismissal.
- **Background Service Onboarding Sheet**: Converted `BackgroundPermissionDialog` into an interactive bottom sheet guiding merchants through SMS permissions, battery optimization exemptions, and OEM autostart configuration (Xiaomi HyperOS/MIUI, Samsung, Huawei).

### ⚡ Build & Performance Improvements
- Upgraded Gradle daemon memory parameters (`-Xmx4096m -XX:MaxMetaspaceSize=1024m`) for accelerated R8 shrink passes.
- 100% pass rate across the comprehensive domain & data unit test suite.

---

## 📦 Download APKs / دانلود فایل‌های نصبی

| Architecture / نوع معماری | File Name | Size | Recommended For |
| :--- | :--- | :--- | :--- |
| **ARM 64-bit (Modern)** | `app-prod-arm64-v8a-release.apk` | 1.99 MB | اکثر گوشی‌های مدرن (Samsung, Xiaomi, Pixel) |
| **ARM 32-bit (Legacy)** | `app-prod-armeabi-v7a-release.apk` | 1.99 MB | گوشی‌ها و کارت‌خوان‌های قدیمی اندرویدی |
| **Universal (All-in-One)** | `app-prod-universal-release.apk` | 2.05 MB | سازگار با تمامی گوشی‌ها و پردازنده‌ها |
| **x86 64-bit (PC / Emulator)** | `app-prod-x86_64-release.apk` | 2.00 MB | شبیه‌سازهای کامپیوتر ۶۴ بیتی و ChromeOS |
| **x86 32-bit (PC / Emulator)** | `app-prod-x86-release.apk` | 1.99 MB | شبیه‌سازهای کامپیوتر ۳۲ بیتی |

---

## 🔒 Signature Verification & SHA-256 Checksums / اعتبارسنجی امضا و هش فایل‌ها

All APK binaries are cryptographically signed with **v1 (JAR)**, **v2 (APK Signature Scheme v2)**, and **v3 (APK Signature Scheme v3)**:

```text
app-prod-arm64-v8a-release.apk   : 44E41371C236FE18053E1E6C5EAF99E0DBE6CB5D7F87666DE498CF7488543077
app-prod-armeabi-v7a-release.apk : 3AB79213EF92578E54545A86A64ABCA902BA92085100929CE8D821090B6967BA
app-prod-universal-release.apk   : 3F6B8D6D0E8507B367093ED9311DA72CC8A9A81FC804110D618FDCEC0B4323AD
app-prod-x86-release.apk         : 35E161AA7DE20A451FA011F97701E50E465284A1F432B75443D84492C271ED8E
app-prod-x86_64-release.apk      : 6B2B8FE81B7090820C342AA05D5D54651D74216AD3576B5BACB3A2695A2CB171
```

---

## 🛡️ Privacy & Compliance
- **Zero Advertising / Zero Analytics SDKs**: No Google Firebase, no third-party trackers, no telemetry.
- **Strictly No SMS Send Permission**: The app requires `RECEIVE_SMS` & `READ_SMS` only.
- **Local SQLite DB with SQLCipher**: Encrypted on-device transaction ledger.
