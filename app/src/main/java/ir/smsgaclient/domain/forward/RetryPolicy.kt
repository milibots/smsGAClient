package ir.smsgaclient.domain.forward

import java.util.concurrent.TimeUnit

object RetryPolicy {

    const val MAX_ATTEMPTS = 8
    const val BATTERY_THRESHOLD_PERCENT = 15

    fun getDelayMillis(attempt: Int): Long {
        return when (attempt) {
            1 -> 0L
            2 -> TimeUnit.SECONDS.toMillis(30)
            3 -> TimeUnit.MINUTES.toMillis(2)
            4 -> TimeUnit.MINUTES.toMillis(10)
            5 -> TimeUnit.HOURS.toMillis(1)
            6 -> TimeUnit.HOURS.toMillis(6)
            7 -> TimeUnit.HOURS.toMillis(24)
            else -> -1L
        }
    }

    fun shouldRetry(httpStatusCode: Int?): Boolean {
        if (httpStatusCode == null) return true

        return when (httpStatusCode) {
            200, 201, 204 -> false
            400, 403 -> false
            401 -> false
            408, 429 -> true
            in 500..599 -> true
            else -> false
        }
    }

    fun shouldPauseForBattery(batteryPercent: Int, isCharging: Boolean): Boolean {
        if (isCharging) return false
        return batteryPercent < BATTERY_THRESHOLD_PERCENT
    }
}
