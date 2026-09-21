# 📡 smsGAClient — API Contract

This document defines the network interfaces for **smsGAClient**: Webhook Forwarding, Pairing, and Remote Patterns Sync.

---

## 1. Webhook Forwarding (Bridge → Merchant Webhook)

The merchant phone POSTs this JSON payload for each parsed transaction.

### 1.1 Endpoint
- **Method**: `POST`
- **URL**: Merchant-configured URL (e.g. `https://merchant.example.com/api/v1/sms-hook` or local `http://192.168.1.100:8000/webhook`)
- **Protocol**: HTTP and HTTPS supported (HTTPS recommended for production)

### 1.2 Headers
```http
Content-Type: application/json
Authorization: Bearer <api_token>
X-SmsGA-Signature: hmac-sha256=<hex_signature>
X-SmsGA-Device: <device_uuid>
X-SmsGA-Timestamp: <unix_seconds>
User-Agent: smsGAClient/<version> (Android <sdk>)
```

### 1.3 Signature Scheme
```
signature = HMAC_SHA256(hmac_secret, "<timestamp>.<body_json>")
```
- `<timestamp>`: Decimal Unix epoch seconds matching `X-SmsGA-Timestamp`.
- `<body_json>`: Raw UTF-8 payload bytes exactly as sent.
- Format: `hmac-sha256=<hex_digest_lowercase>`.

### 1.4 Request Payload
```json
{
  "event": "sms.deposit",
  "message_id": "sha256:abc123456789abcdef0123456789abcdef0123456789abcdef0123456789abcd",
  "device_id": "a1b2c3d4-e5f6-4a1b-8c2d-1234567890ab",
  "merchant_id": "m_8821",
  "received_at": "2026-09-21T14:32:11+03:30",
  "bank": "blu",
  "type": "deposit",
  "amount_rial": 4500280,
  "amount_toman": 450028,
  "card_last4": "1234",
  "balance_rial": 125000000,
  "raw_sms": "+۴٬۵۰۰٬۲۸۰ ریال — بلو",
  "parse_version": 41
}
```

### 1.5 Response Handling
| HTTP Status | Action Taken | Retry? |
|-------------|--------------|--------|
| `200`, `201`, `204` | Mark transaction `SENT`. Notify merchant. | No |
| `400`, `403` | Mark transaction `FAILED`. No retry. Show error notification. | No |
| `401` | Clear stored credentials. Trigger re-pairing flow. | No |
| `408`, `429` | Transient error. Retry respecting `Retry-After` header if present. | Yes |
| `5xx` / Connection error | Retry with exponential backoff schedule. | Yes |

---

## 2. Remote Patterns Sync API (Server → App)

Fetches bank SMS parsing regex rules.

### 2.1 Endpoint
- **Method**: `GET`
- **URL**: `https://api.smsga.ir/v1/patterns`
- **Cache Header**: `If-None-Match: "<version>"`

### 2.2 Response (`200 OK`)
```json
{
  "version": 41,
  "updated_at": "2026-09-21T10:00:00Z",
  "banks": [
    {
      "id": "blu",
      "name_fa": "بلو",
      "senders": ["BLUBANK", "983000..."],
      "detect_regex": "(بلو|blu)",
      "deposit_regex": "\\+(?<amount>\\d+)\\s*ریال",
      "withdraw_regex": "-(?<amount>\\d+)\\s*ریال",
      "card_regex": "(?<card>\\*{0,4}\\d{4})",
      "balance_regex": "موجودی[:\\s]*(?<balance>\\d+)"
    }
  ]
}
```

---

## 3. Pairing API (App ↔ Merchant Server)

Pairs merchant device in under 120 seconds.

### 3.1 Initiate Pairing / Poll Status
- **Method**: `POST`
- **URL**: `https://api.smsga.ir/v1/pair/poll`
- **Request**:
```json
{
  "device_id": "a1b2c3d4-e5f6-4a1b-8c2d-1234567890ab",
  "pairing_code": "482193",
  "app_version": "1.0.0"
}
```
- **Response (`200 OK` when paired)**:
```json
{
  "status": "paired",
  "merchant_id": "m_8821",
  "webhook_url": "https://merchant.example.com/api/v1/sms-hook",
  "api_token": "tkn_sec_991823749182",
  "hmac_secret": "hex_secret_key_value"
}
```
- **Response (`202 Accepted` while waiting)**:
```json
{
  "status": "pending",
  "retry_after_seconds": 2
}
```
