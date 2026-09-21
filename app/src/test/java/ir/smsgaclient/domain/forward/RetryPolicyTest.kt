// app/src/test/java/ir/smsgaclient/domain/forward/RetryPolicyTest.kt
package ir.smsgaclient.domain.forward

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class RetryPolicyTest {

    @Test
    fun `verifies exact retry schedule backoff delays`() {
        assertEquals(0L, RetryPolicy.getDelayMillis(1))
        assertEquals(TimeUnit.SECONDS.toMillis(30), RetryPolicy.getDelayMillis(2))
        assertEquals(TimeUnit.MINUTES.toMillis(2), RetryPolicy.getDelayMillis(3))
        assertEquals(TimeUnit.MINUTES.toMillis(10), RetryPolicy.getDelayMillis(4))
        assertEquals(TimeUnit.HOURS.toMillis(1), RetryPolicy.getDelayMillis(5))
        assertEquals(TimeUnit.HOURS.toMillis(6), RetryPolicy.getDelayMillis(6))
        assertEquals(TimeUnit.HOURS.toMillis(24), RetryPolicy.getDelayMillis(7))
        assertEquals(-1L, RetryPolicy.getDelayMillis(8))
        assertEquals(-1L, RetryPolicy.getDelayMillis(9))
    }

    @Test
    fun `evaluates retryable and non-retryable http statuses`() {
        // 2xx -> no retry
        assertFalse(RetryPolicy.shouldRetry(200))
        assertFalse(RetryPolicy.shouldRetry(201))
        assertFalse(RetryPolicy.shouldRetry(204))

        // 400, 401, 403 -> no retry
        assertFalse(RetryPolicy.shouldRetry(400))
        assertFalse(RetryPolicy.shouldRetry(401))
        assertFalse(RetryPolicy.shouldRetry(403))

        // 408, 429, 5xx -> retry
        assertTrue(RetryPolicy.shouldRetry(408))
        assertTrue(RetryPolicy.shouldRetry(429))
        assertTrue(RetryPolicy.shouldRetry(500))
        assertTrue(RetryPolicy.shouldRetry(502))
        assertTrue(RetryPolicy.shouldRetry(503))

        // Network error (null status) -> retry
        assertTrue(RetryPolicy.shouldRetry(null))
    }

    @Test
    fun `pauses on low battery when not charging`() {
        // Battery < 15% and not charging -> pause
        assertTrue(RetryPolicy.shouldPauseForBattery(batteryPercent = 14, isCharging = false))
        assertTrue(RetryPolicy.shouldPauseForBattery(batteryPercent = 5, isCharging = false))

        // Battery < 15% but charging -> do NOT pause
        assertFalse(RetryPolicy.shouldPauseForBattery(batteryPercent = 14, isCharging = true))

        // Battery >= 15% -> do NOT pause
        assertFalse(RetryPolicy.shouldPauseForBattery(batteryPercent = 15, isCharging = false))
        assertFalse(RetryPolicy.shouldPauseForBattery(batteryPercent = 80, isCharging = false))
    }
}
