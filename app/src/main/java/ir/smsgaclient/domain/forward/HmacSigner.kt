// app/src/main/java/ir/smsgaclient/domain/forward/HmacSigner.kt
package ir.smsgaclient.domain.forward

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Computes HMAC-SHA256 signatures for webhook payloads.
 * Strictly adheres to SYSTEM_PROMPT.md §7.3:
 * signature = HMAC_SHA256(hmac_secret, "<timestamp>.<body_json>")
 */
object HmacSigner {

    private const val ALGORITHM = "HmacSHA256"

    /**
     * Signs the message using the provided secret key.
     *
     * @param secret Plaintext or hex secret key
     * @param timestamp Unix epoch timestamp in seconds
     * @param bodyJson The exact raw UTF-8 JSON payload string
     * @return Formatted signature string: "hmac-sha256=<hex_digest>"
     */
    fun sign(secret: String, timestamp: Long, bodyJson: String): String {
        val payloadToSign = "$timestamp.$bodyJson"
        val keySpec = SecretKeySpec(secret.toByteArray(Charsets.UTF_8), ALGORITHM)
        val mac = Mac.getInstance(ALGORITHM)
        mac.init(keySpec)
        val rawHmac = mac.doFinal(payloadToSign.toByteArray(Charsets.UTF_8))
        val hex = rawHmac.joinToString("") { "%02x".format(it) }
        return "hmac-sha256=$hex"
    }
}
