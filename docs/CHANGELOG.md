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
