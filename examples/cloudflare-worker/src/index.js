/**
 * smsGAClient Cloudflare Worker - Serverless Webhook Receiver & Simulator
 * 
 * Production-ready serverless webhook receiver with:
 * - HMAC-SHA256 signature verification (via Web Crypto API)
 * - Anti-Replay Attack timestamp verification (±300s window)
 * - Deduplication (Idempotency) via Cloudflare KV
 * - Live Dark-Mode Dashboard for testing
 * - Optional instant Telegram Bot alerts
 */

export default {
  async fetch(request, env, ctx) {
    const url = new URL(request.url);

    // 1. Dashboard UI (GET /)
    if (request.method === "GET" && (url.pathname === "/" || url.pathname === "/dashboard")) {
      return handleDashboard(request, env);
    }

    // 2. Query transactions API (GET /api/transactions)
    if (request.method === "GET" && url.pathname === "/api/transactions") {
      return handleGetTransactions(request, env);
    }

    // 3. Webhook Receiver (POST /webhook)
    if (request.method === "POST" && (url.pathname === "/webhook" || url.pathname === "/api/webhook")) {
      return handleWebhook(request, env);
    }

    // 4. Default 404
    return new Response(JSON.stringify({ error: "Endpoint not found" }), {
      status: 404,
      headers: { "Content-Type": "application/json" }
    });
  }
};

/**
 * Handle incoming SMS webhook from smsGAClient phone
 */
async function handleWebhook(request, env) {
  const apiToken = env.API_TOKEN || "smsga_demo_token_123456";
  const hmacSecret = env.HMAC_SECRET || "smsga_demo_secret_abcdef";

  const authHeader = request.headers.get("Authorization");
  const signatureHeader = request.headers.get("X-SmsGA-Signature");
  const timestampHeader = request.headers.get("X-SmsGA-Timestamp");
  const deviceId = request.headers.get("X-SmsGA-Device") || "unknown";

  // 1. Bearer Token Verification
  if (!authHeader || authHeader !== `Bearer ${apiToken}`) {
    return new Response(JSON.stringify({ error: "Unauthorized: Invalid API Token" }), {
      status: 401,
      headers: { "Content-Type": "application/json" }
    });
  }

  // 2. Anti-Replay Attack: Check Timestamp (±300 seconds)
  const currentTimestamp = Math.floor(Date.now() / 1000);
  const requestTimestamp = parseInt(timestampHeader || "0", 10);
  if (!requestTimestamp || Math.abs(currentTimestamp - requestTimestamp) > 300) {
    return new Response(JSON.stringify({ error: "Bad Request: Timestamp expired or skewed" }), {
      status: 400,
      headers: { "Content-Type": "application/json" }
    });
  }

  // 3. Read Raw Body
  const rawBody = await request.text();

  // 4. HMAC-SHA256 Signature Verification
  if (!signatureHeader || !signatureHeader.startsWith("hmac-sha256=")) {
    return new Response(JSON.stringify({ error: "Bad Request: Missing or malformed signature" }), {
      status: 400,
      headers: { "Content-Type": "application/json" }
    });
  }

  const clientSignature = signatureHeader.replace("hmac-sha256=", "").trim();
  const isValidSignature = await verifyHmacSha256(hmacSecret, `${requestTimestamp}.${rawBody}`, clientSignature);

  if (!isValidSignature) {
    return new Response(JSON.stringify({ error: "Unauthorized: HMAC signature mismatch" }), {
      status: 401,
      headers: { "Content-Type": "application/json" }
    });
  }

  // 5. Parse Payload
  let payload;
  try {
    payload = JSON.parse(rawBody);
  } catch (e) {
    return new Response(JSON.stringify({ error: "Invalid JSON payload" }), {
      status: 400,
      headers: { "Content-Type": "application/json" }
    });
  }

  // 6. Test SMS Event Handling
  if (payload.event === "test") {
    return new Response(JSON.stringify({
      status: "success",
      event: "test",
      message: "Test SMS verified successfully by Cloudflare Worker!"
    }), {
      status: 200,
      headers: { "Content-Type": "application/json" }
    });
  }

  // 7. Deduplication & KV Storage
  const messageId = payload.message_id || `msg_${Date.now()}`;
  
  if (env.SMSGA_KV) {
    // Check if already processed
    const existing = await env.SMSGA_KV.get(`tx:${messageId}`);
    if (existing) {
      return new Response(JSON.stringify({ status: "duplicate", message: "Transaction already recorded" }), {
        status: 200,
        headers: { "Content-Type": "application/json" }
      });
    }

    const txRecord = {
      ...payload,
      received_server_at: new Date().toISOString()
    };

    // Store in KV with 14 days expiration
    await env.SMSGA_KV.put(`tx:${messageId}`, JSON.stringify(txRecord), { expirationTtl: 86400 * 14 });

    // Update index of recent message IDs
    const indexRaw = await env.SMSGA_KV.get("tx_index");
    let indexList = indexRaw ? JSON.parse(indexRaw) : [];
    indexList.unshift(messageId);
    if (indexList.length > 50) indexList = indexList.slice(0, 50);
    await env.SMSGA_KV.put("tx_index", JSON.stringify(indexList));
  }

  // 8. Optional Telegram Bot Notification
  if (env.TELEGRAM_BOT_TOKEN && env.TELEGRAM_CHAT_ID) {
    sendTelegramAlert(payload, env);
  }

  return new Response(JSON.stringify({
    status: "success",
    message_id: messageId,
    amount_toman: payload.amount_toman,
    bank: payload.bank,
    processed_at: new Date().toISOString()
  }), {
    status: 200,
    headers: { "Content-Type": "application/json" }
  });
}

/**
 * List recent transactions from KV
 */
async function handleGetTransactions(request, env) {
  if (!env.SMSGA_KV) {
    return new Response(JSON.stringify({ transactions: [], note: "KV namespace not configured" }), {
      status: 200,
      headers: { "Content-Type": "application/json" }
    });
  }

  const indexRaw = await env.SMSGA_KV.get("tx_index");
  const indexList = indexRaw ? JSON.parse(indexRaw) : [];

  const transactions = [];
  for (const id of indexList.slice(0, 20)) {
    const item = await env.SMSGA_KV.get(`tx:${id}`);
    if (item) transactions.push(JSON.parse(item));
  }

  return new Response(JSON.stringify({ transactions }), {
    status: 200,
    headers: { "Content-Type": "application/json", "Access-Control-Allow-Origin": "*" }
  });
}

/**
 * Web Crypto HMAC-SHA256 Signature Verifier
 */
async function verifyHmacSha256(secret, data, expectedHex) {
  const encoder = new TextEncoder();
  const keyData = encoder.encode(secret);
  const messageData = encoder.encode(data);

  const cryptoKey = await crypto.subtle.importKey(
    "raw",
    keyData,
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"]
  );

  const signatureBuffer = await crypto.subtle.sign("HMAC", cryptoKey, messageData);
  const hashArray = Array.from(new Uint8Array(signatureBuffer));
  const computedHex = hashArray.map(b => b.toString(16).padStart(2, "0")).join("");

  return computedHex.toLowerCase() === expectedHex.toLowerCase();
}

/**
 * Send Telegram Alert
 */
async function sendTelegramAlert(payload, env) {
  try {
    const toman = payload.amount_toman ? payload.amount_toman.toLocaleString("fa-IR") : "نامشخص";
    const bank = payload.bank || "نامشخص";
    const card = payload.card_last4 ? `•••• ${payload.card_last4}` : "نامشخص";

    const text = `💰 *واریز جدید در smsGAClient*\n\n` +
      `💵 *مبلغ:* ${toman} تومان\n` +
      `🏦 *بانک:* ${bank}\n` +
      `💳 *کارت:* \`${card}\`\n` +
      `🕒 *زمان:* ${payload.received_at || new Date().toLocaleTimeString("fa-IR")}\n\n` +
      `📩 *متن پیامک:*\n\`${payload.raw_sms || ""}\``;

    const tgUrl = `https://api.telegram.org/bot${env.TELEGRAM_BOT_TOKEN}/sendMessage`;
    await fetch(tgUrl, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        chat_id: env.TELEGRAM_CHAT_ID,
        text: text,
        parse_mode: "Markdown"
      })
    });
  } catch (e) {
    // Non-blocking
  }
}

/**
 * Render Live Dashboard
 */
async function handleDashboard(request, env) {
  let txHtml = "";
  if (env.SMSGA_KV) {
    const indexRaw = await env.SMSGA_KV.get("tx_index");
    const indexList = indexRaw ? JSON.parse(indexRaw) : [];
    for (const id of indexList.slice(0, 15)) {
      const item = await env.SMSGA_KV.get(`tx:${id}`);
      if (item) {
        const tx = JSON.parse(item);
        txHtml += `
          <div class="card">
            <div class="header">
              <span class="badge bank">${tx.bank || "بانک"}</span>
              <span class="badge time">${tx.received_at || ""}</span>
            </div>
            <div class="amount">+${(tx.amount_toman || 0).toLocaleString()} <span class="unit">تومان</span></div>
            <div class="detail">کارت: •••• ${tx.card_last4 || "----"} | شناسه: <code>${(tx.message_id || "").slice(0, 18)}...</code></div>
            <div class="raw">${tx.raw_sms || ""}</div>
          </div>
        `;
      }
    }
  }

  if (!txHtml) {
    txHtml = `<div class="empty">هنوز هیچ پیامکی دریافت نشده است. پیامک‌های ارسالی از گوشی اینجا نمایش داده می‌شوند.</div>`;
  }

  const html = `<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>smsGAClient Webhook Dashboard</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/rastikerdar/vazirmatn@v33.003/Vazirmatn-font-face.css">
  <style>
    :root {
      --bg: #0A0A0C;
      --card-bg: #141418;
      --border: #26262B;
      --primary: #FFFFFF;
      --secondary: #9E9EA8;
      --green: #10B981;
    }
    * { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Vazirmatn', sans-serif; }
    body { background-color: var(--bg); color: var(--primary); padding: 24px 16px; display: flex; justify-content: center; }
    .container { width: 100%; max-width: 680px; }
    .hero { background: var(--card-bg); border: 1px solid var(--border); border-radius: 24px; padding: 24px; margin-bottom: 24px; text-align: center; }
    .hero h1 { font-size: 22px; font-weight: 800; margin-bottom: 8px; }
    .hero p { color: var(--secondary); font-size: 14px; margin-bottom: 16px; }
    .webhook-box { background: #000000; border: 1px dashed var(--border); border-radius: 16px; padding: 12px; font-family: monospace; font-size: 13px; color: var(--green); word-break: break-all; direction: ltr; }
    .section-title { font-size: 16px; font-weight: 700; margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center; }
    .card { background: var(--card-bg); border: 1px solid var(--border); border-radius: 20px; padding: 16px; margin-bottom: 12px; transition: transform 0.2s; }
    .card:hover { transform: translateY(-2px); }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
    .badge { padding: 4px 10px; border-radius: 12px; font-size: 11px; font-weight: 600; }
    .badge.bank { background: #22222A; color: #E0E0E6; }
    .badge.time { color: var(--secondary); }
    .amount { font-size: 20px; font-weight: 800; color: var(--green); margin-bottom: 6px; }
    .amount .unit { font-size: 13px; font-weight: normal; color: var(--secondary); }
    .detail { font-size: 13px; color: var(--secondary); margin-bottom: 8px; }
    .raw { background: #000000; border-radius: 10px; padding: 8px 12px; font-size: 12px; color: #B0B0BA; line-height: 1.6; }
    .empty { background: var(--card-bg); border: 1px solid var(--border); border-radius: 20px; padding: 40px; text-align: center; color: var(--secondary); font-size: 14px; }
  </style>
</head>
<body>
  <div class="container">
    <div class="hero">
      <h1>🚀 سرور دریافت وب‌هوک smsGAClient</h1>
      <p>این صفحه روی کلودفلر ورکر (Cloudflare Workers) اجرا می‌شود و وب‌هوک‌های دریافتی را ذخیره می‌کند.</p>
      <div class="webhook-box">POST https://${new URL(request.url).host}/webhook</div>
    </div>
    <div class="section-title">
      <span>تراکنش‌های دریافتی اخیر</span>
      <span style="font-size: 12px; color: var(--green); cursor: pointer;" onclick="location.reload()">بروزرسانی ↻</span>
    </div>
    ${txHtml}
  </div>
</body>
</html>`;

  return new Response(html, {
    status: 200,
    headers: { "Content-Type": "text/html; charset=utf-8" }
  });
}
