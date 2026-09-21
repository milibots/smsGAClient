// app/src/test/java/ir/smsgaclient/domain/forward/HmacSignerTest.kt
package ir.smsgaclient.domain.forward

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class HmacSignerTest {

    @Test
    fun `generates valid hmac sha256 signature matching expected hex digest`() {
        val secret = "my_super_secret_key"
        val timestamp = 1790000000L
        val body = "{\"event\":\"sms.deposit\",\"amount_rial\":50000}"

        val signature = HmacSigner.sign(secret, timestamp, body)

        assertTrue(signature.startsWith("hmac-sha256="))

        // Verify with independent calculation
        val expectedPayload = "$timestamp.$body"
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secret.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        val expectedDigest = mac.doFinal(expectedPayload.toByteArray(Charsets.UTF_8))
        val expectedHex = expectedDigest.joinToString("") { "%02x".format(it) }

        assertEquals("hmac-sha256=$expectedHex", signature)
    }

    @Test
    fun `different body or timestamp produces different signature`() {
        val secret = "my_super_secret_key"
        val sig1 = HmacSigner.sign(secret, 1000L, "body1")
        val sig2 = HmacSigner.sign(secret, 1000L, "body2")
        val sig3 = HmacSigner.sign(secret, 1001L, "body1")

        assertTrue(sig1 != sig2)
        assertTrue(sig1 != sig3)
    }
}
