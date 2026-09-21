# 🏛️ smsGAClient — System Architecture & Overview

**smsGAClient** turns an Iranian merchant's Android phone into a resilient, offline-first card-to-card payment cockpit.

---

## 1. The Two Layers

### Layer 1: The Bridge (Invisible, Always On)
- **Goal**: Never miss, never lose, and reliably forward any bank SMS.
- **Workflow**:
  1. `SmsReceiver` (BroadcastReceiver, priority 999) catches `SMS_RECEIVED_ACTION`.
  2. Raw SMS is stored in Room DB with status `RECEIVED` before any processing.
  3. `ParseWorker` normalizes Persian/Arabic digits and extracts amount, bank, and card last4 using remote pattern rules.
  4. `ForwardWorker` signs payload with HMAC-SHA256 and POSTs to merchant HTTPS webhook.
  5. Exponential backoff retry engine survives network dropouts and Doze mode.

### Layer 2: The Cockpit (Merchant Dashboard)
- **Goal**: Complete, beautiful RTL point-of-sale dashboard.
- **Tabs**:
  1. **Home (خانه)**: Today's sales in Toman, % vs yesterday, transaction counter, queue count, real-time updates.
  2. **Transactions (تراکنش)**: Filterable, searchable history with CSV export and raw/parsed inspection.
  3. **Cards (کارتها)**: Multi-card receiving management, daily limit tracking, warning states (90% / 100%), Tehran midnight auto-reset.
  4. **Settings (تنظیمات)**: Webhook validation, security keys, quiet hours, test SMS simulator.

---

## 2. Security Guarantees
- **No full card numbers**: Only `last4` is retained.
- **EncryptedSharedPreferences**: AES256-GCM backed by Android Keystore.
- **HMAC-SHA256 signatures**: `timestamp.body` signing prevents MITM and tampering.
- **Strict permissions**: No `SEND_SMS`, no external storage, cleartext disabled.
