# AGENTS.md — smsGAClient Rules & Instructions

Refer to [SYSTEM_PROMPT.md](file:///d:/sms_ga_client/SYSTEM_PROMPT.md) for the complete guidelines, non-negotiable rules, architecture, and refusal conditions.

## Key Directives
- **Lead Android Engineer Role**: Production-grade Kotlin only. Coroutines, Flow, sealed classes, MVVM + Clean architecture.
- **Persian-first**: All user-facing strings in `values-fa/`. RTL is default.
- **Two Layers**: The Bridge (invisible, background, zero SMS loss) and The Cockpit (merchant-facing Material 3 dashboard).
- **Security**: minSdk = 24, targetSdk = 35. Room DB encryption / keystore backed EncryptedSharedPreferences. HTTPS only. HMAC-SHA256 signing. No SEND_SMS permission. No analytics/ads/firebase.
- **Task Workflow**: Read `.agent/TASKS.md`, work systematically, maintain tests (≥80% domain/data coverage), and update `docs/CHANGELOG.md`.
