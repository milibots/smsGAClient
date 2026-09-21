# 📝 smsGAClient — Changelog

All notable changes to **smsGAClient** will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0-alpha01] - 2026-09-21

### Added
- **Core Architecture & Build**:
  - Gradle Kotlin DSL version catalog (`gradle/libs.versions.toml`) with Java 17 toolchain and `minSdk = 24`, `targetSdk = 35`.
  - Flavors `dev` and `prod`, with release R8 full mode configuration.
  - Manifest with strict security permissions (`RECEIVE_SMS`, `READ_SMS`, `FOREGROUND_SERVICE_DATA_SYNC`, `RECEIVE_BOOT_COMPLETED`). Strictly no `SEND_SMS` or dangerous storage permissions.
  - Network security config with cleartext traffic disabled and cert pinning ready.

- **The Bridge (Background SMS Ingestion & Forwarding)**:
  - `SmsReceiver`: Priority 999 receiver with `goAsync()` Room DB persistence before any network dispatch (Zero SMS loss).
  - `SmsNormalizer`: 5-step normalization pipeline (Persian/Arabic digits to Latin, Arabic letters to Persian, thousand separators removal, whitespace trimming).
  - `BankDetector`: Bank identification engine supporting sender names/numbers and regex matching.
  - `SmsParser`: Safe parsing engine extracting Rial/Toman amounts, card last 4 digits, and balances without ever throwing.
  - `HmacSigner`: Cryptographic HMAC-SHA256 signature generator (`timestamp.body`).
  - `PayloadBuilder`: Canonical JSON payload generator matching API contract.
  - `RetryPolicy`: Exponential backoff schedule (immediate, 30s, 2m, 10m, 1h, 6h, 24h) with battery-aware pause (<15% when not charging).
  - `ParseWorker` & `ForwardWorker`: WorkManager coroutine workers handling resilient background execution and offline queuing.
  - `SmsForegroundService` & `BootReceiver`: Low-importance persistent background service surviving reboots.

- **Data & Storage**:
  - Room Database (`SmsGaDatabase`) with 6 entities (`raw_sms`, `parsed_sms`, `transactions`, `cards`, `forward_attempts`, `patterns_cache`).
  - Coroutine suspend and Flow reactive queries.
  - `SecurePrefs`: MasterKey AES256-GCM Keystore-backed encrypted storage for webhook URLs and tokens.

- **The Cockpit (Merchant UI)**:
  - Persian-first Material 3 design system with RTL layout mirroring.
  - Navigation Compose graph with 4-tab bottom navigation.
  - **Home Tab**: Today's sales in Toman, transaction count, queue count, recent transactions, Bridge status indicator (Green/Yellow/Red/Gray).
  - **Transactions Tab**: Search bar, date filter chips (امروز, هفته, ماه, همه), transaction rows.
  - **Transaction Detail**: Raw SMS view, extracted parameters, manual retry forward button.
  - **Cards Tab**: Card list with daily limit progress bar, 90% (yellow) and 100% (red) warning indicators, Add Card dialog.
  - **Settings Tab**: HTTPS-enforced webhook URL input, masked API token, test SMS button, toggle filters, device ID and pairing revocation.
  - **Onboarding Flow**: 5-step wizard (Welcome, SMS permission blocking, Notifications, Battery exemption, 6-digit Pair code).

- **Testing & CI/CD**:
  - Comprehensive unit test suite with 100% pass rate: `SmsNormalizerTest`, `BankDetectorTest`, `ParserDepositTest`, `ParserWithdrawTest`, `ParserUnknownTest`, `ParserIdempotencyTest`, `HmacSignerTest`, `PayloadBuilderTest`, `RetryPolicyTest`, `PersianNumberFormatterTest`.
  - GitHub Actions workflow (`.github/workflows/ci.yml`).

## [1.0.0-alpha02] - 2026-09-21

### Changed & Enhanced (UI/UX Pro Max & Websima Design Elevation)
- **Design Intelligence Applied** (`ui-ux-pro-max` + `websima-ui-skills`):
  - **OLED Dark Mode Tokens**: Enhanced `Color.kt` with Midnight Background (`#0A0F1D`), Card Surface (`#162032`), Elevated Surface (`#1E2B42`), and Iranian Bank Brand Colors (Pasargad, Blu, Mellat, Saman, Melli, Sepah, Tejarat, Keshavarzi, Parsian, Refah, Shahr, etc.).
  - **Persian Typography System**: Calibrated line heights to 1.65–1.8x on all body copy, eliminating diacritic and character clipping (گ، ژ، پ، چ، ی). Added distinct typography tokens for card numbers, currency badges, and code snippets.
  - **Animated Bridge Status Dot**: Integrated infinite transition glowing heartbeat pulse in `BridgeStatusBar` when forwarding is active, with merchant status subtext.
  - **Sales Hero Card**: Elevated with gradient mesh overlay, Toman headline with separate currency styling, Rial subtitle, and average ticket calculation pill.
  - **Iranian Bank Card Aesthetic**: Built `IranianBankCard` and `BankCardWithQuotaCard` with EMV chip simulation, masked LTR card numbers (`•••• •••• •••• ۱۲۳۴`), cardholder name, and animated daily quota progress bars.
  - **Receipt-Style Transaction Detail**: Added structured bank transaction receipt, bank avatars, copyable message IDs, and raw SMS code block.
  - **Touch & Accessibility Ergonomics**: Verified strict touch targets ≥ 48dp on all interactive elements, AutoMirrored icons for RTL consistency, and high-contrast WCAG AAA text/container pairs.
  - **Release APK**: Verified R8 full mode shrinking producing a **1.53 MB** production APK (well under the 8 MB budget).

## [1.0.0] - 2026-09-21

### Added
- **Multi-Architecture Release Builds (ABI Splits)**:
  - `arm64-v8a`: 1.56 MB (Modern 64-bit Android devices)
  - `armeabi-v7a`: 1.56 MB (Legacy 32-bit Android phones and POS hardware)
  - `universal`: 1.61 MB (All-in-one APK compatible across all Android architectures)
  - `x86_64`: 1.56 MB (64-bit Android emulators / ChromeOS)
  - `x86`: 1.56 MB (32-bit Android emulators)
- **GitHub Release Automation**:
  - Published production repository to [`milibots/smsGAClient`](https://github.com/milibots/smsGAClient).
  - Published official release tag `v1.0.0` with full asset attachments and verified SHA-256 checksums.
  - Configured installable debug signing for release build flavor to allow direct installation without signature errors.
- **Background Reliability & Battery Optimization (Doze Mode Exemption)**:
  - Added `PermissionManager`: Centralized checker and intent launcher for SMS permissions, Battery Optimization exemption (`ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`), and manufacturer-specific AutoStart settings (Xiaomi MIUI/HyperOS, Samsung, Huawei, Oppo, Vivo).
  - Added `BackgroundPermissionWarningBanner`: Auto-updating Cockpit banner warning the merchant if SMS or battery exemption are disabled.
  - Added `BackgroundSetupModalDialog`: High-touch Persian modal dialog explaining the necessity of background execution with 1-tap activation.
  - Updated `OnboardingScreen` and `SettingsScreen` with live status indicators and direct exemption request triggers.

## [1.0.1] - 2026-09-21

### Changed & Improved
- **Codebase Streamlining**: Stripped all internal developer comments and docstrings across all 56 Kotlin source files for production-grade cleanliness.
- **Repository Optimization**: Removed temporary agent queues (`.agent/`), `.cursorrules`, and `CLAUDE.md`. Updated `.gitignore` with comprehensive exclusions.
- **Fresh Multi-Architecture APK Builds**:
  - `arm64-v8a`: 1.57 MB (SHA256: `A22D4B0E5EB563D3...`)
  - `armeabi-v7a`: 1.57 MB (SHA256: `389F60031D5C4D96...`)
  - `universal`: 1.62 MB (SHA256: `A29A608A1CD16433...`)
  - `x86_64`: 1.57 MB (SHA256: `DC1A483E9A175FC9...`)
  - `x86`: 1.57 MB (SHA256: `99B29D728F4BEFAA...`)

## [1.0.2] - 2026-09-21

### Changed & Enhanced (Monochrome Luxury & Jetpack Compose UI)
- **Monochrome Luxury Design System**: Pure OLED Black (`#000000`), deep obsidian cards, brushed titanium bank cards, and high-contrast Black-on-White CTA actions.
- **Floating Pill Bottom Navigation**: Floating capsule container with 34dp corner radius and smooth spring-animated selection capsule.
- **Titanium Specular Sheen**: Continuous specular shine sweep across Iranian bank cards and sales hero dashboard.
- **Horizontal Pager Card Carousel**: Fluid horizontal card swiping with dynamic animated pill page indicators.
- **Animated Segmented Date Filters**: Sliding capsule segmented control with instant tactile haptic feedback.
- **Expandable Transaction Cards**: In-place detail expansion with spring physics revealing raw Message ID and 1-tap copy.
- **Native Tactile Haptics**: Integrated on navigation pills, filter tabs, and card interactions.
- **Scalable Vector Logo**: In-app metallic geometric logo asset (`MonochromeLogo.kt`).

## [1.0.3] - 2026-09-21

### Added & Improved (Native Typography & Material 3 Sheets)
- **Vazirmatn Persian Typography Family**: Bundled authentic Iranian font family `Vazirmatn` across 5 weights (Light, Regular, Medium, SemiBold, Bold) in `res/font/`, linked to `CockpitTypography`. Flawless Persian diacritics and glyph rendering without clipping.
- **Material 3 Modal Bottom Sheets**:
  - **CardsScreen**: Transformed "Add Card" dialog into a fluid `ModalBottomSheet` with drag handle, bank selection chips, and edge-to-edge navigation bar padding.
  - **SettingsScreen**: Modernized "Revoke Pairing" confirmation into a dedicated bottom sheet with warning badge and safe coroutine dismiss.
  - **Background Setup Sheet**: Redesigned `BackgroundPermissionDialog` into a bottom sheet sheet flow with OEM autostart guidance and battery exemption action buttons.
- **Build & Memory Optimization**:
  - Upgraded Gradle daemon heap (`4096m`) and max metaspace (`1024m`) in `gradle.properties`.
  - Upgraded release build artifacts to signed v1.0.3 with full ABI split support.



