// app/src/main/java/ir/smsgaclient/domain/forward/RetryPolicy.kt
package ir.smsgaclient.domain.forward

import java.util.concurrent.TimeUnit

/**
 * Exponential backoff retry policy for webhook forwarding.
 * Strictly adheres to SYSTEM_PROMPT.md §7.5.
 */
object RetryPolicy {

    const val MAX_ATTEMPTS = 8
    const val BATTERY_THRESHOLD_PERCENT = 15

    /**
     * Calculates the backoff delay in milliseconds for the given attempt count.
     *
     * @param attempt 1-indexed attempt number
     * @return delay in milliseconds, or -1 if attempts exceeded
     */
    fun getDelayMillis(attempt: Int): Long {
        return when (attempt) {
            1 -> 0L
            2 -> TimeUnit.SECONDS.toMillis(30)
            3 -> TimeUnit.MINUTES.toMillis(2)
            4 -> TimeUnit.MINUTES.toMillis(10)
            5 -> TimeUnit.HOURS.toMillis(1)
            6 -> TimeUnit.HOURS.toMillis(6)
            7 -> TimeUnit.HOURS.toMillis(24)
            else -> -1L // Exceeded max attempts
        }
    }

    /**
     * Determines if a webhook HTTP response status should trigger a retry.
     *
     * @param httpStatusCode HTTP response status code
     * @return true if retryable, false if permanent success or failure
     */
    fun shouldRetry(httpStatusCode: Int?): Boolean {
        if (httpStatusCode == null) return true // Network exception / timeout -> retry

        return when (httpStatusCode) {
            200, 201, 204 -> false // Success
            400, 403 -> false     // Client error -> no retry, mark FAILED
            401 -> false          // Auth error -> trigger re-pair
            408, 429 -> true      // Timeout or Rate limited -> retry
            in 500..599 -> true   // Server errors -> retry
            else -> false
        }
    }

    /**
     * Checks whether retry should be paused due to low battery and not charging.
     *
     * @param batteryPercent Current battery percentage (0..100)
     * @param isCharging Whether phone is plugged in
     * @return true if retries should be paused
     */
    fun shouldPauseForBattery(batteryPercent: Int, isCharging: Boolean): Boolean {
        if (isCharging) return false
        return batteryPercent < BATTERY_THRESHOLD_PERCENT
    }
}
