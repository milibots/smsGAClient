<div align="center">

<img src="docs/assets/app_logo.jpg" alt="smsGA Logo" width="160" style="border-radius: 28px; box-shadow: 0 12px 36px rgba(0,0,0,0.5);" />

# smsGAClient (اس‌ام‌اس جی‌ای)

### سامانه مدیریت کارت‌به‌کارت و بریج ارسال وب‌هوک پیامک‌های بانکی برای پذیرندگان ایرانی
**Production-Grade Android Card-to-Card Payment Cockpit & Background SMS Forwarding Bridge**

[![GitHub Release](https://img.shields.io/github/v/release/milibots/smsGAClient?style=for-the-badge&color=10B981)](https://github.com/milibots/smsGAClient/releases/latest)
[![Android](https://img.shields.io/badge/Android-7.0%2B%20(API%2024--35)-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-Production-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Material 3](https://img.shields.io/badge/Material%203-Persian--First-008080?style=for-the-badge)](https://m3.material.io)

<br/>

<img src="docs/assets/hero_banner.jpg" alt="smsGA Cockpit & Bridge Banner" width="100%" style="border-radius: 16px; margin: 16px 0;" />

</div>

---

## 📖 معرفی سامانه / Overview

**smsGAClient** یک اپلیکیشن بومی اندروید به زبان کاتلین است که گوشی پذیرنده یا فروشگاه را به یک **درگاه اختصاصی و پایانه هوشمند مدیریت پرداخت کارت‌به‌کارت** تبدیل می‌کند. این برنامه با دریافت پیامک‌های واریزی بانک‌های مختلف ایران، آن‌ها را به صورت آنی تجزیه و اعتبارسنجی کرده و از طریق وب‌هوک امن امضا شده با `HMAC-SHA256` به سرور فروشگاه شما ارسال می‌کند.

<div align="center">
  <img src="docs/assets/onboarding_illustration.jpg" alt="Instant Bank SMS Detection" width="80%" style="border-radius: 16px;" />
</div>

---

## 🌟 ویژگی‌های کلیدی / Key Features

### ۱. موتور بریج پس‌زمینه بدون قطعی (The Bridge)
- **Zero SMS Loss Architecture**: اولویت بالای BroadcastReceiver (`priority = 999`) + سرویس پیش‌زمینه فعال + WorkManager جهت تلاش مجدد هوشمند.
- **معافیت خودکار از بهینه‌سازی باتری (Doze Mode Bypass)**: درخواست مستقیم مجوز فعالیت بدون محدودیت جهت جلوگیری از توقف برنامه در زمان قفل بودن صفحه.
- **راهنمای شروع خودکار اختصاصی (OEM AutoStart)**: پشتیبانی و انتقال خودکار به تنظیمات گوشی‌های شیائومی (MIUI / HyperOS Autostart)، سامسونگ، هواوی، اوپو و ویوو.
- **امضای امنیتی HMAC-SHA256**: تمام بسته‌های ارسالی به وب‌هوک دارای امضای رمزنگاری‌شده بر پایه کلید مخفی پذیرنده هستند.

### ۲. داشبورد و کاک‌پیت پذیرنده (The Cockpit)
- **طراحی متریال ۳ اختصاصی فارسی**: هماهنگ با استانداردهای مدرن فین‌تک، دارک مود واقعی (Midnight OLED) و تایپوگرافی بدون بریدگی حروف.
- **پایش سقف تراکنش کارت‌ها**: نمایش هوشمند پر شدن سقف روزانه واریز کارت‌ها (۱ میلیارد ریال) همراه با هشدار رنگی زرد (۹۰٪) و قرمز (۱۰۰٪).
- **رسید دیجیتال بانکی**: نمایش تراکنش‌ها در قالب رسید حرارتی خوانا با قابلیت کپی سریع شماره پیگیری و شماره کارت.
- **گاوصندوق سخت‌افزاری**: ذخیره امن آدرس وب‌هوک و کلیدهای ارتباطی با رمزنگاری `AES256-GCM` متصل به Android Keystore.

---

## 🏦 بانک‌های تحت پوشش / Supported Banks

| بانک | نماد | پشتیبانی واریز / برداشت | استخراج مانده و پیگیری |
| :--- | :---: | :---: | :---: |
| **بلوبانک (Blu Bank)** | 🔵 | ✅ | ✅ |
| **بانک سامان (Saman)** | 🔷 | ✅ | ✅ |
| **بانک پاسارگاد (Pasargad)** | 🟡 | ✅ | ✅ |
| **بانک ملت (Mellat)** | 🔴 | ✅ | ✅ |
| **بانک سپه (Sepah)** | ⚪ | ✅ | ✅ |
| **بانک ملی ایران (Melli)** | 🟢 | ✅ | ✅ |
| **بانک صادرات، پارسیان، تجارت، شهر، کشاورزی، آینده، رفاه** | 🏦 | ✅ | ✅ |

---

## 📦 دانلود نسخه‌های نصبی / Download APKs

تمامی فایل‌های نصبی زیر با معماری تفکیک شده (ABI Splits)، بدون کدهای اضافه و بهینه شده با موتور R8 آماده نصب مستقیم روی گوشی هستند:

| نوع پردازنده / معماری | فایل نصبی | حجم | لینک دانلود مستقیم |
| :--- | :--- | :---: | :---: |
| **ARM 64-bit (گوشی‌های جدید)** | `app-prod-arm64-v8a-release.apk` | **1.57 MB** | [دانلود نسخه ARM64](https://github.com/milibots/smsGAClient/releases/latest/download/app-prod-arm64-v8a-release.apk) |
| **ARM 32-bit (گوشی‌های قدیمی و پوز)** | `app-prod-armeabi-v7a-release.apk` | **1.57 MB** | [دانلود نسخه ARMv7a](https://github.com/milibots/smsGAClient/releases/latest/download/app-prod-armeabi-v7a-release.apk) |
| **یونیورسال (همه‌کاره برای تمامی گوشی‌ها)** | `app-prod-universal-release.apk` | **1.62 MB** | [دانلود نسخه یونیورسال](https://github.com/milibots/smsGAClient/releases/latest/download/app-prod-universal-release.apk) |
| **x86_64 (شبیه‌ساز کامپیوتر)** | `app-prod-x86_64-release.apk` | **1.57 MB** | [دانلود نسخه x86_64](https://github.com/milibots/smsGAClient/releases/latest/download/app-prod-x86_64-release.apk) |
| **x86 (شبیه‌ساز ۳۲ بیتی)** | `app-prod-x86-release.apk` | **1.57 MB** | [دانلود نسخه x86](https://github.com/milibots/smsGAClient/releases/latest/download/app-prod-x86-release.apk) |

---

## 🔒 امنیت و حریم خصوصی / Security & Privacy
- 🚫 **بدون هیچ‌گونه تبلیغات یا ابزار ردیابی**: فاقد گوگل فایربیس، آنالیتیکس یا هرگونه کتابخانه متفرقه.
- 🚫 **عدم دسترسی به ارسال پیامک**: برنامه تنها مجوز `RECEIVE_SMS` و `READ_SMS` را دارد و از نظر سیستمی امکان ارسال پیامک یا ایجاد هزینه ندارد.
- 🔐 **دیتابیس رمزنگاری شده آفلاین**: کلیه لاگ‌ها و رکوردهای تراکنش به صورت محلی در پایگاه داده SQLite دستگاه پذیرنده ذخیره می‌شوند.

---

## 🛠️ ساخت از سورس‌کد / Build from Source

```bash
# کلون کردن مخزن
git clone https://github.com/milibots/smsGAClient.git
cd smsGAClient

# اجرای تست‌های خودکار
./gradlew testDevDebugUnitTest

# ساخت خروجی نهایی ریلیز
./gradlew assembleProdRelease
```
