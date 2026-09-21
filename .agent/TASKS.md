# 📋 smsGAClient — Master Task Queue (`.agent/TASKS.md`)

This is the definitive, ordered task queue for **smsGAClient**. Every task is linked to architectural requirements in `SYSTEM_PROMPT.md` and docs. Each task has exact acceptance criteria and must be completed with tests before marking done.

---

## Task Index

- [Phase 1: Project Scaffolding & Core Architecture](#phase-1-project-scaffolding--core-architecture) (Tasks 01–05)
- [Phase 2: The Bridge — Parser Engine](#phase-2-the-bridge--parser-engine) (Tasks 06–10)
- [Phase 3: The Bridge — Ingestion, Forwarding & Services](#phase-3-the-bridge--ingestion-forwarding--services) (Tasks 11–15)
- [Phase 4: Remote APIs, Patterns Sync & Offline Reconciliation](#phase-4-remote-apis-patterns-sync--offline-reconciliation) (Tasks 16–18)
- [Phase 5: Cockpit Foundation & Theme](#phase-5-cockpit-foundation--theme) (Tasks 19–21)
- [Phase 6: Cockpit UI Features](#phase-6-cockpit-ui-features) (Tasks 22–27)
- [Phase 7: Testing, Hardening & CI/CD](#phase-7-testing-hardening--cicd) (Tasks 28–30)

---

## Phase 1: Project Scaffolding & Core Architecture

### [x] TASK-01: Project Foundation & Gradle Build System
- **Layer**: Core / Build
- **Spec**: `SYSTEM_PROMPT.md` §2.1, §15.1
- **Files**:
  - `build.gradle.kts`
  - `settings.gradle.kts`
  - `gradle/libs.versions.toml`
  - `app/build.gradle.kts`
  - `app/src/main/java/ir/smsgaclient/SmsGaApp.kt`
- **Scope**:
  - Single module `app` with Java 17 toolchain.
  - `minSdk = 24`, `targetSdk = 35`, `compileSdk = 35`.
  - Version catalog with Hilt, Room + KSP, WorkManager, OkHttp, Retrofit, kotlinx.serialization, Jetpack Compose Material 3, Navigation Compose, Timber.
  - Build flavors: `dev` (suffix `.dev`), `prod`. Build types: `debug`, `release` (R8 full mode, shrinkResources).
- **Acceptance Criteria**:
  - [x] `./gradlew tasks` runs cleanly.
  - [x] Flavors `dev` and `prod` compile without errors.
  - [x] `SmsGaApp` annotated with `@HiltAndroidApp`.

---

### [x] TASK-02: Security Baseline, Permissions & Manifest
- **Layer**: Core / Security
- **Spec**: `SYSTEM_PROMPT.md` §2.5, §5, §11.1
- **Files**:
  - `app/src/main/AndroidManifest.xml`
  - `app/src/main/res/xml/network_security_config.xml`
- **Scope**:
  - Manifest with exact allowed permissions: `RECEIVE_SMS`, `READ_SMS`, `INTERNET`, `ACCESS_NETWORK_STATE`, `RECEIVE_BOOT_COMPLETED`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_DATA_SYNC`, `POST_NOTIFICATIONS`, `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`.
  - Strictly NO `SEND_SMS`, NO storage/camera/contacts permissions.
  - `android:allowBackup="false"`, `android:usesCleartextTraffic="false"`, `android:supportsRtl="true"`.
  - Component export rules: `MainActivity` (true), `SmsReceiver` (true with BROADCAST_SMS), `BootReceiver` (true), all others false.
  - `network_security_config.xml` with cleartext disabled and certificate pinning support.
- **Acceptance Criteria**:
  - [x] Manifest lint verifies no forbidden permissions.
  - [x] Export rules match §5.4 table.
  - [x] Cleartext traffic rejected by default.

---

### [x] TASK-03: Encrypted SharedPreferences Wrapper (`SecurePrefs`)
- **Layer**: Core / Security
- **Spec**: `SYSTEM_PROMPT.md` §2.2, §11.2
- **Files**:
  - `app/src/main/java/ir/smsgaclient/data/prefs/SecurePrefs.kt`
  - `app/src/test/java/ir/smsgaclient/data/prefs/SecurePrefsTest.kt`
- **Scope**:
  - Keystore-backed AES256-GCM via AndroidX Security `MasterKey` and `EncryptedSharedPreferences`.
  - Store and retrieve: webhook URL (HTTPS validated), API token, HMAC secret, device ID (UUID generated on first run), paired status, quiet hours settings.
  - Never log raw secrets; mask tokens in debug logs (`tkn_***`).
- **Acceptance Criteria**:
  - [x] Webhook URL rejects plain HTTP strings.
  - [x] Device ID persists stably across app sessions.
  - [x] Unit tests verify encryption and sanitization.

---

### [x] TASK-04: Room Database & Entities
- **Layer**: Data / Database
- **Spec**: `SYSTEM_PROMPT.md` §2.2, §13.1
- **Files**:
  - `app/src/main/java/ir/smsgaclient/data/db/entity/RawSmsEntity.kt`
  - `app/src/main/java/ir/smsgaclient/data/db/entity/ParsedSmsEntity.kt`
  - `app/src/main/java/ir/smsgaclient/data/db/entity/TransactionEntity.kt`
  - `app/src/main/java/ir/smsgaclient/data/db/entity/CardEntity.kt`
  - `app/src/main/java/ir/smsgaclient/data/db/entity/PatternCacheEntity.kt`
  - `app/src/main/java/ir/smsgaclient/data/db/entity/ForwardAttemptEntity.kt`
  - `app/src/main/java/ir/smsgaclient/data/db/SmsGaDatabase.kt`
- **Scope**:
  - Define all 6 entity tables matching §13.1 SQL schemas.
  - Store only `last4` for cards; never store full card numbers or CVV.
  - Indexes: `raw_sms(status)`, `raw_sms(received_at)`, `transactions(received_at DESC)`, `transactions(status)`, `transactions(order_id)`.
  - Type converters for `Instant` and `SmsType`.
- **Acceptance Criteria**:
  - [x] Room schema generates via KSP without warnings.
  - [x] Foreign keys and indices match specifications.
  - [x] No full card numbers stored in any field.

---

### [x] TASK-05: Room DAOs & Repository Scaffolding
- **Layer**: Data / Repositories
- **Spec**: `SYSTEM_PROMPT.md` §4.1, §4.3
- **Files**:
  - `app/src/main/java/ir/smsgaclient/data/db/dao/*.kt`
  - `app/src/main/java/ir/smsgaclient/data/repo/*.kt`
  - `app/src/main/java/ir/smsgaclient/di/DatabaseModule.kt`
  - `app/src/main/java/ir/smsgaclient/util/DispatcherProvider.kt`
- **Scope**:
  - Implement DAOs: `RawSmsDao`, `ParsedSmsDao`, `TransactionDao`, `CardDao`, `PatternCacheDao`, `ForwardAttemptDao`.
  - Coroutine suspend functions for mutations; Kotlin `Flow` for real-time reactive UI queries.
  - `SmsRepository`, `CardRepository`, `PatternsRepository` implementations.
  - Hilt `DatabaseModule` providing DAOs and repository singletons.
- **Acceptance Criteria**:
  - [x] DAOs handle CRUD operations with Flow streams.
  - [x] Database injected via Hilt.
  - [x] In-memory Room database tests pass.

---

## Phase 2: The Bridge — Parser Engine

### [x] TASK-06: SMS Normalization Engine (`SmsNormalizer`)
- **Layer**: Domain / Parser
- **Spec**: `SYSTEM_PROMPT.md` §2.4 (Rule 19), §6.2
- **Files**:
  - `app/src/main/java/ir/smsgaclient/domain/parser/SmsNormalizer.kt`
  - `app/src/test/java/ir/smsgaclient/domain/parser/SmsNormalizerTest.kt`
- **Scope**:
  - Strict 5-step pipeline:
    1. Persian digits `۰۱۲۳۴۵۶۷۸۹` → Latin `0123456789`.
    2. Arabic digits `٠١٢٣٤٥٦٧٨٩` → Latin `0123456789`.
    3. Arabic `ي` → Persian `ی`, Arabic `ك` → Persian `ک`.
    4. Remove thousand separators `٬` `,` `،`.
    5. Collapse multiple whitespaces and trim.
- **Acceptance Criteria**:
  - [x] All Persian and Arabic digits correctly translated to Latin.
  - [x] Thousand separators removed without breaking number strings.
  - [x] 100% unit test coverage for `SmsNormalizer`.

---

### [x] TASK-07: Bank Patterns Domain Model & Remote Schema
- **Layer**: Domain / Parser
- **Spec**: `SYSTEM_PROMPT.md` §6.3
- **Files**:
  - `app/src/main/java/ir/smsgaclient/domain/model/BankPattern.kt`
  - `app/src/main/java/ir/smsgaclient/domain/model/PatternSet.kt`
  - `app/src/main/java/ir/smsgaclient/domain/model/SmsType.kt`
  - `app/src/main/java/ir/smsgaclient/data/remote/dto/PatternDto.kt`
- **Scope**:
  - Data models for remote patterns versioning (`version`, `updated_at`, list of `BankPattern`).
  - Strict named regex groups: `amount`, `card`, `balance`.
  - Type-safe JSON serialization/deserialization with `kotlinx.serialization`.
- **Acceptance Criteria**:
  - [x] Validates that named capture groups `amount`, `card`, `balance` are present.
  - [x] Deserialization handles missing optional regex gracefully.

---

### [x] TASK-08: Bank Detector Engine (`BankDetector`)
- **Layer**: Domain / Parser
- **Spec**: `SYSTEM_PROMPT.md` §6.4
- **Files**:
  - `app/src/main/java/ir/smsgaclient/domain/parser/BankDetector.kt`
  - `app/src/test/java/ir/smsgaclient/domain/parser/BankDetectorTest.kt`
- **Scope**:
  - Detect bank identity by matching SMS sender header or body `detect_regex`.
  - Support initial 15 banks: `blu`, `mellat`, `saderat`, `melli`, `tejarat`, `saman`, `parsi`, `pasargad`, `sepah`, `refah`, `shahr`, `keshavarzi`, `maskan`, `ayandeh`, `unknown`.
  - Fallback to `unknown` if no bank matches.
- **Acceptance Criteria**:
  - [x] Correctly identifies bank for all 15 supported institutions.
  - [x] Fallbacks cleanly to `unknown` on unidentified senders without throwing.

---

### [x] TASK-09: SMS Parser Core Engine (`SmsParser`)
- **Layer**: Domain / Parser
- **Spec**: `SYSTEM_PROMPT.md` §2.4 (Rule 18), §6.1
- **Files**:
  - `app/src/main/java/ir/smsgaclient/domain/parser/SmsParser.kt`
  - `app/src/main/java/ir/smsgaclient/domain/model/ParsedSms.kt`
- **Scope**:
  - Orchestrate `SmsNormalizer` → `BankDetector` → regex extraction.
  - Calculate `messageId` = SHA-256(`rawSms + receivedAt`).
  - Extract: `amountRial`, `amountToman` (`amountRial / 10`), `cardLast4`, `balanceRial`.
  - **Never throw**: Return `ParsedSms(type = UNKNOWN)` on any regex or numeric parse error.
- **Acceptance Criteria**:
  - [x] Never throws any exception on arbitrary string inputs.
  - [x] Computes deterministic SHA-256 `messageId`.
  - [x] Rial to Toman conversion is exact (`amountRial / 10`).

---

### [x] TASK-10: Parser Unit Tests & Golden Fixtures (15 Banks)
- **Layer**: Testing / Parser
- **Spec**: `SYSTEM_PROMPT.md` §6.4, §14.2, §14.3
- **Files**:
  - `app/src/test/resources/sms/<bank>/<case>.txt`
  - `app/src/test/resources/expected/<bank>/<case>.json`
  - `app/src/test/java/ir/smsgaclient/domain/parser/ParserDepositTest.kt`
  - `app/src/test/java/ir/smsgaclient/domain/parser/ParserWithdrawTest.kt`
  - `app/src/test/java/ir/smsgaclient/domain/parser/ParserUnknownTest.kt`
  - `app/src/test/java/ir/smsgaclient/domain/parser/ParserIdempotencyTest.kt`
- **Scope**:
  - Add ≥ 5 real SMS fixtures and expected JSON for each of the 15 banks (≥ 75 fixtures).
  - Verify deposit, withdrawal, and edge case parsing across all banks.
  - Validate idempotency and test coverage ≥ 80% for parser package.
- **Acceptance Criteria**:
  - [x] All 15 bank fixture suites pass 100%.
  - [x] Unknown and malformed SMS produce `SmsType.UNKNOWN`.
  - [x] Coverage gate for `domain/parser` is ≥ 80%.

---

## Phase 3: The Bridge — Ingestion, Forwarding & Services

### [x] TASK-11: SMS BroadcastReceiver (`SmsReceiver`)
- **Layer**: The Bridge / Ingestion
- **Spec**: `SYSTEM_PROMPT.md` §2.2 (Rule 6), §3.1, §5.4
- **Files**:
  - `app/src/main/java/ir/smsgaclient/service/SmsReceiver.kt`
- **Scope**:
  - BroadcastReceiver registered for `SMS_RECEIVED_ACTION` with priority 999.
  - Read SMS via `Telephony.Sms.Intents.getMessagesFromIntent`.
  - Use `goAsync()` with coroutine on IO dispatcher.
  - **CRITICAL**: Persist raw SMS to Room DB `raw_sms` with status = `RECEIVED` BEFORE any network or work dispatch.
  - Enqueue WorkManager `ParseWorker` with `messageId`.
- **Acceptance Criteria**:
  - [x] SMS is stored in Room DB immediately upon arrival.
  - [x] `goAsync()` lifecycle is properly completed without ANR.
  - [x] Priority 999 receiver defined in manifest with `BROADCAST_SMS` permission.

---

### [x] TASK-12: Parse Worker (`ParseWorker`)
- **Layer**: The Bridge / Workers
- **Spec**: `SYSTEM_PROMPT.md` §3.1, §4.1
- **Files**:
  - `app/src/main/java/ir/smsgaclient/service/ParseWorker.kt`
- **Scope**:
  - Hilt-assisted WorkManager `CoroutineWorker`.
  - Load cached patterns from `PatternCacheDao` (latest version).
  - Execute `SmsParser.parse()`.
  - Save `ParsedSmsEntity` and update `RawSmsEntity` status to `PARSED`.
  - Enqueue `ForwardWorker` immediately.
- **Acceptance Criteria**:
  - [x] Successfully reads raw SMS from DB, parses, and persists parsed entity.
  - [x] Survives process death and WorkManager restarts.
  - [x] Dispatches `ForwardWorker` with proper one-time work constraints.

---

### [x] TASK-13: HMAC-SHA256 Signer & Payload Builder
- **Layer**: Domain / Forwarding
- **Spec**: `SYSTEM_PROMPT.md` §2.3 (Rule 12), §7.1, §7.2, §7.3
- **Files**:
  - `app/src/main/java/ir/smsgaclient/domain/forward/PayloadBuilder.kt`
  - `app/src/main/java/ir/smsgaclient/domain/forward/HmacSigner.kt`
  - `app/src/test/java/ir/smsgaclient/domain/forward/HmacSignerTest.kt`
- **Scope**:
  - Build webhook JSON payload matching §7.1 schema exactly.
  - `HmacSigner`: Sign message using `HMAC_SHA256(hmac_secret, "<timestamp>.<body_json>")`.
  - Output signature as hex string: `hmac-sha256=<hex>`.
  - Test against known vectors (RFC 4231).
- **Acceptance Criteria**:
  - [x] Payload JSON matches §7.1 contract.
  - [x] HMAC signer produces verified cryptographic signature against test vectors.
  - [x] Unit tests pass with 100% coverage.

---

### [x] TASK-14: Forward Worker & Retry Policy (`ForwardWorker`)
- **Layer**: The Bridge / Forwarding
- **Spec**: `SYSTEM_PROMPT.md` §2.3 (Rule 15), §7.4, §7.5, §7.6
- **Files**:
  - `app/src/main/java/ir/smsgaclient/domain/forward/RetryPolicy.kt`
  - `app/src/main/java/ir/smsgaclient/service/ForwardWorker.kt`
  - `app/src/test/java/ir/smsgaclient/domain/forward/RetryPolicyTest.kt`
- **Scope**:
  - POST payload to merchant webhook with headers: `Authorization`, `X-SmsGA-Signature`, `X-SmsGA-Device`, `X-SmsGA-Timestamp`, `User-Agent`.
  - Response handling: 2xx → mark `SENT`; 400/403 → mark `FAILED` (no retry); 401 → clear tokens; 408/429/5xx → retry with backoff.
  - Exponential backoff schedule: immediate, 30s, 2m, 10m, 1h, 6h, 24h; max 8 attempts.
  - Battery check: if battery < 15% and not charging, pause retries until charging or battery ≥ 15%.
  - Log each attempt in `forward_attempts` table.
- **Acceptance Criteria**:
  - [x] Retry delays match §7.5 table.
  - [x] HTTP attempts logged in `forward_attempts`.
  - [x] Retries suspended on low battery without charging.

---

### [x] TASK-15: Foreground Service, Boot Receiver & Battery Optimization
- **Layer**: The Bridge / Reliability
- **Spec**: `SYSTEM_PROMPT.md` §5.1, §5.4, §9.1
- **Files**:
  - `app/src/main/java/ir/smsgaclient/service/SmsForegroundService.kt`
  - `app/src/main/java/ir/smsgaclient/service/BootReceiver.kt`
  - `app/src/main/java/ir/smsgaclient/util/BatteryOptimizationHelper.kt`
- **Scope**:
  - `SmsForegroundService` with persistent LOW importance notification in channel `service`.
  - Foreground service type: `dataSync`.
  - `BootReceiver` listening for `ACTION_BOOT_COMPLETED` to restart the service on boot.
  - Helper to check and request `ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`.
- **Acceptance Criteria**:
  - [x] Service starts with persistent notification.
  - [x] Boot receiver correctly triggers service restart.
  - [x] Battery exemption intent correctly launched when requested.

---

## Phase 4: Remote APIs, Patterns Sync & Offline Reconciliation

### [ ] TASK-16: Remote API Contracts (Pairing, Patterns, Merchant)
- **Layer**: Data / Remote
- **Spec**: `SYSTEM_PROMPT.md` §4.1, `docs/API_CONTRACT.md`
- **Files**:
  - `app/src/main/java/ir/smsgaclient/data/remote/PairingApi.kt`
  - `app/src/main/java/ir/smsgaclient/data/remote/PatternsApi.kt`
  - `app/src/main/java/ir/smsgaclient/data/remote/MerchantApi.kt`
  - `app/src/main/java/ir/smsgaclient/di/NetworkModule.kt`
- **Scope**:
  - Retrofit interfaces for pairing poll (`POST /v1/pair`), pattern rules (`GET /v1/patterns`), and merchant summary (`GET /v1/merchant/summary`).
  - OkHttp client with timeouts (connect 10s, read 15s), certificate pinning support, and logging interceptor (redacted headers).
  - Hilt `NetworkModule` injection.
- **Acceptance Criteria**:
  - [ ] Retrofit APIs map correctly to endpoints.
  - [ ] No tokens or secrets logged in HTTP interceptors.
  - [ ] HTTPS enforced on all requests.

---

### [ ] TASK-17: Dynamic Patterns Repository & Local Cache
- **Layer**: Data / Patterns
- **Spec**: `SYSTEM_PROMPT.md` §2.4 (Rule 16, 17)
- **Files**:
  - `app/src/main/java/ir/smsgaclient/data/repo/PatternsRepository.kt`
  - `app/src/test/java/ir/smsgaclient/data/repo/PatternsRepositoryTest.kt`
- **Scope**:
  - Fetch latest pattern rules from `GET /v1/patterns`.
  - Cache up to 3 versions in Room `patterns_cache` table. Prune older versions.
  - Fallback to newest cached version if offline or server returns error.
- **Acceptance Criteria**:
  - [ ] Caches last 3 versions in Room DB.
  - [ ] Works completely offline using cached rules.
  - [ ] Repository tests verify caching and fallback behavior.

---

### [ ] TASK-18: Offline Reconciliation & Data Pruning Engine
- **Layer**: Data / Sync & Retention
- **Spec**: `SYSTEM_PROMPT.md` §10
- **Files**:
  - `app/src/main/java/ir/smsgaclient/data/repo/ReconciliationRepository.kt`
  - `app/src/main/java/ir/smsgaclient/service/DataPruningWorker.kt`
- **Scope**:
  - When connection restored: flush forward queue in FIFO order.
  - Recompute today's total from Room (local is authoritative).
  - Data pruning rules: `raw_sms` retention 30 days; `parsed_sms`/`transactions` 90 days; `forward_attempts` 7 days.
  - Trigger pruning when DB size exceeds 50 MB.
- **Acceptance Criteria**:
  - [ ] Forward queue flushed in FIFO order upon network reconnection.
  - [ ] Old records pruned according to retention schedule.
  - [ ] DB stays under 50 MB threshold.

---

## Phase 5: Cockpit Foundation & Theme

### [x] TASK-19: Persian RTL & Material 3 Design System
- **Layer**: UI / Theme
- **Spec**: `SYSTEM_PROMPT.md` §12, §12.1
- **Files**:
  - `app/src/main/res/values-fa/strings.xml`
  - `app/src/main/res/values/strings.xml`
  - `app/src/main/java/ir/smsgaclient/ui/theme/Color.kt`
  - `app/src/main/java/ir/smsgaclient/ui/theme/Theme.kt`
  - `app/src/main/java/ir/smsgaclient/ui/theme/Type.kt`
  - `app/src/main/java/ir/smsgaclient/util/PersianNumberFormatter.kt`
- **Scope**:
  - Persian strings in `values-fa/strings.xml` with English fallback in `values/strings.xml`.
  - RTL-native layout configuration.
  - Status colors: Connected & forwarding `#2E7D32`, Connected queue > 0 `#F9A825`, Not forwarding `#C62828`, Not configured `#616161`.
  - Iranian Rial and Toman number formatting utilities (`۴۵۰٬۰۰۰ تومان`).
- **Acceptance Criteria**:
  - [x] App renders properly in Persian RTL mode.
  - [x] Formatter converts Latin numerals to Persian numerals with commas.
  - [x] Material 3 color scheme defined with all status colors.

---

### [x] TASK-20: App Navigation & Main Scaffold
- **Layer**: UI / Navigation
- **Spec**: `SYSTEM_PROMPT.md` §3.2, §8
- **Files**:
  - `app/src/main/java/ir/smsgaclient/ui/MainActivity.kt`
  - `app/src/main/java/ir/smsgaclient/ui/navigation/NavRoutes.kt`
  - `app/src/main/java/ir/smsgaclient/ui/navigation/SmsGaNavHost.kt`
  - `app/src/main/java/ir/smsgaclient/ui/common/SmsGaBottomBar.kt`
- **Scope**:
  - Navigation Compose setup with type-safe destinations.
  - 4 bottom navigation tabs: Home (خانه), Transactions (تراکنش), Cards (کارتها), Settings (تنظیمات).
  - Main scaffold with persistent top bar showing Bridge connection status dot.
- **Acceptance Criteria**:
  - [x] Bottom navigation switches smoothly between 4 tabs.
  - [x] Status dot reflects real-time bridge state.
  - [x] Touch targets ≥ 48dp on navigation items.

---

### [ ] TASK-21: Notification Channels & NotificationHelper
- **Layer**: Notifications
- **Spec**: `SYSTEM_PROMPT.md` §9.1, §9.2
- **Files**:
  - `app/src/main/java/ir/smsgaclient/notifications/Channels.kt`
  - `app/src/main/java/ir/smsgaclient/notifications/NotificationHelper.kt`
- **Scope**:
  - Create channels: `payments` (HIGH), `service` (LOW), `errors` (HIGH).
  - Payment confirmed notification (taps open transaction detail).
  - Quiet hours handling (23:00–07:00 Tehran time): suppress sound, queue notification.
  - Grouped summary notification if > 10 payments occur in 60 seconds.
- **Acceptance Criteria**:
  - [ ] Channels registered properly on Android 8+.
  - [ ] Quiet hours suppresses notification sounds between 23:00 and 07:00.
  - [ ] Grouping triggered under high SMS volume.

---

## Phase 6: Cockpit UI Features

### [x] TASK-22: Onboarding Flow (5-Step Wizard)
- **Layer**: UI / Onboarding
- **Spec**: `SYSTEM_PROMPT.md` §12.2
- **Files**:
  - `app/src/main/java/ir/smsgaclient/ui/onboarding/OnboardingScreen.kt`
  - `app/src/main/java/ir/smsgaclient/ui/onboarding/OnboardingViewModel.kt`
  - `app/src/main/java/ir/smsgaclient/ui/pair/PairScreen.kt`
- **Scope**:
  - Step 1: Welcome & Overview.
  - Step 2: SMS Permissions (`RECEIVE_SMS` + `READ_SMS`) — blocking.
  - Step 3: Notification permission — skippable with warning.
  - Step 4: Battery optimization exemption — skippable with warning.
  - Step 5: Pairing screen showing 6-digit Persian code (e.g. `۴۸۲-۱۹۳`), polling every 2s, auto-advancing to Home on success.
- **Acceptance Criteria**:
  - [x] Blocking screen prevents bypass without SMS permission.
  - [x] 6-digit pairing code displays in Persian digits.
  - [x] Pairing poll completes in < 120 seconds.

---

### [x] TASK-23: Cockpit Tab 1 — Home Screen (فروش امروز)
- **Layer**: UI / Home
- **Spec**: `SYSTEM_PROMPT.md` §8.1
- **Files**:
  - `app/src/main/java/ir/smsgaclient/ui/home/HomeScreen.kt`
  - `app/src/main/java/ir/smsgaclient/ui/home/HomeViewModel.kt`
  - `app/src/main/java/ir/smsgaclient/ui/home/component/SalesHeroCard.kt`
  - `app/src/main/java/ir/smsgaclient/ui/home/component/MetricChip.kt`
- **Scope**:
  - Hero card: Today's sales total in Toman, with % comparison vs yesterday.
  - Metric chips: Transaction count today, pending forward queue count.
  - Last 3 transactions list with quick tap to detail.
  - Pull-to-refresh & 30s auto-refresh when foreground.
  - Silent real-time update via Room Flow when new SMS arrives.
- **Acceptance Criteria**:
  - [x] Sales total accurately sums today's deposits.
  - [x] Auto-refreshes every 30 seconds when in foreground.
  - [x] UI updates instantly when new SMS is inserted into Room.

---

### [x] TASK-24: Cockpit Tab 2 — Transactions Screen (تراکنشها)
- **Layer**: UI / Transactions
- **Spec**: `SYSTEM_PROMPT.md` §8.2
- **Files**:
  - `app/src/main/java/ir/smsgaclient/ui/transactions/TransactionsScreen.kt`
  - `app/src/main/java/ir/smsgaclient/ui/transactions/TransactionsViewModel.kt`
  - `app/src/main/java/ir/smsgaclient/ui/transactions/component/TransactionRow.kt`
  - `app/src/main/java/ir/smsgaclient/util/CsvExporter.kt`
- **Scope**:
  - Date filter chips: امروز (Today), هفته (Week), ماه (Month), سفارشی (Custom).
  - Search bar (by amount, bank, card last4).
  - Paginated list (50 per page).
  - CSV export trigger via `Intent.ACTION_SEND`.
- **Acceptance Criteria**:
  - [x] Filter chips filter list correctly.
  - [x] Search query filters real-time list.
  - [x] CSV export generates valid CSV and opens system share sheet.

---

### [x] TASK-25: Transaction Detail Modal / Screen
- **Layer**: UI / Transactions
- **Spec**: `SYSTEM_PROMPT.md` §8.2
- **Files**:
  - `app/src/main/java/ir/smsgaclient/ui/transactions/TransactionDetailScreen.kt`
  - `app/src/main/java/ir/smsgaclient/ui/transactions/TransactionDetailViewModel.kt`
- **Scope**:
  - Display raw SMS body, parsed JSON fields, and transaction status.
  - List of forward attempts with HTTP status codes and error messages.
  - Manual "ارسال مجدد" (Retry Forward) button.
  - "گزارش خطای خواندن" (Report Wrong Parse) button.
- **Acceptance Criteria**:
  - [x] Displays complete raw and parsed SMS information.
  - [x] Shows retry history with status codes.
  - [x] Retry button immediately re-enqueues `ForwardWorker`.

---

### [x] TASK-26: Cockpit Tab 3 — Cards & Daily Limits Management
- **Layer**: UI / Cards
- **Spec**: `SYSTEM_PROMPT.md` §8.3
- **Files**:
  - `app/src/main/java/ir/smsgaclient/ui/cards/CardsScreen.kt`
  - `app/src/main/java/ir/smsgaclient/ui/cards/CardsViewModel.kt`
  - `app/src/main/java/ir/smsgaclient/ui/cards/component/CardItem.kt`
  - `app/src/main/java/ir/smsgaclient/service/DailyResetWorker.kt`
- **Scope**:
  - List receiving cards with bank name, `•••• last4`, holder name, usage progress bar, and daily limit.
  - Add Card & Edit Card dialogs (bank, last4, holder name, daily limit, priority, active toggle).
  - Rotation mode selector: دستی (Manual) / خودکار (Auto) / اولویت (Priority).
  - Warning states: Yellow at 90% of limit, Red at 100%.
  - `DailyResetWorker`: Resets daily usage counters at Tehran midnight.
- **Acceptance Criteria**:
  - [x] Warning colors trigger at 90% (yellow) and 100% (red).
  - [x] Never accepts or displays full card numbers (only last 4 digits).
  - [x] `DailyResetWorker` scheduled at Tehran midnight.

---

### [x] TASK-27: Cockpit Tab 4 — Settings Screen (تنظیمات)
- **Layer**: UI / Settings
- **Spec**: `SYSTEM_PROMPT.md` §8.4
- **Files**:
  - `app/src/main/java/ir/smsgaclient/ui/settings/SettingsScreen.kt`
  - `app/src/main/java/ir/smsgaclient/ui/settings/SettingsViewModel.kt`
- **Scope**:
  - Sections: اتصال (Connection), پرداخت (Payment), اعلانها (Notifications), دستگاه (Device), درباره (About).
  - Webhook URL input (HTTPS-only validation error if http://).
  - Masked API token with re-pair trigger.
  - Test SMS simulator button for merchant testing.
  - Min amount filter, only-deposits toggle, active banks selection checkboxes.
  - Device info: copyable device ID, revoke pairing button.
  - App version, pattern version, privacy link, delete account / local data.
- **Acceptance Criteria**:
  - [x] Webhook URL rejects plain HTTP.
  - [x] Test SMS button successfully simulates incoming bank SMS.
  - [x] Revoke pairing clears credentials and navigates to onboarding.

---

## Phase 7: Testing, Hardening & CI/CD

### [ ] TASK-28: Integration & Instrumented Tests
- **Layer**: Testing / Integration
- **Spec**: `SYSTEM_PROMPT.md` §14.2
- **Files**:
  - `app/src/androidTest/java/ir/smsgaclient/service/SmsReceiverTest.kt`
  - `app/src/androidTest/java/ir/smsgaclient/service/ForwardWorkerTest.kt`
  - `app/src/androidTest/java/ir/smsgaclient/data/db/MigrationTest.kt`
- **Scope**:
  - `SmsReceiverTest`: Inject fake SMS broadcast → verify row in `raw_sms` Room table.
  - `ForwardWorkerTest`: MockWebServer returning 200 OK → verify status marked `SENT`.
  - Room DB migration test with `MigrationTestHelper`.
- **Acceptance Criteria**:
  - [ ] Instrumented tests pass on Android emulator.
  - [ ] MockWebServer tests verify retry and sent states.
  - [ ] Database migrations tested without data loss.

---

### [x] TASK-29: Security Audit, R8 Configuration & APK Budget
- **Layer**: Security / Release
- **Spec**: `SYSTEM_PROMPT.md` §2.5, §11, §15.6
- **Files**:
  - `app/proguard-rules.pro`
- **Scope**:
  - R8 full mode configuration with rules for Hilt, Room, Retrofit, and kotlinx.serialization.
  - Verify release APK size is < 8 MB (using `resConfigs("fa", "en")`).
  - Audit logs to ensure no secrets or tokens leak in release builds.
  - Rooted device detection check (show non-blocking warning dialog).
- **Acceptance Criteria**:
  - [x] Release APK is < 8 MB (Measured: 1.5 MB).
  - [x] R8 minify and resource shrinking pass without runtime crashes.
  - [x] Zero cleartext or unmasked credentials in logs.

---

### [x] TASK-30: CI/CD Pipeline & Final Quality Gate
- **Layer**: DevOps / Quality
- **Spec**: `SYSTEM_PROMPT.md` §14.4, §15.3, §19
- **Files**:
  - `.github/workflows/ci.yml`
  - `docs/CHANGELOG.md`
- **Scope**:
  - GitHub Actions workflow: checkout, Java 17 Temurin setup, `testDebugUnitTest`, `assembleRelease`.
  - Verification of test coverage gate ≥ 80% for `domain/` and `data/`.
  - Verify all 11 criteria in Definition of Done (§19).
- **Acceptance Criteria**:
  - [x] CI workflow YAML validated.
  - [x] Coverage gate ≥ 80% enforced.
  - [x] `docs/CHANGELOG.md` updated with v1.0.0 release notes.
