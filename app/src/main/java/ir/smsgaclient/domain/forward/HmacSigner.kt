package ir.smsgaclient.domain.forward

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HmacSigner {

    private const val ALGORITHM = "HmacSHA256"

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
