// app/src/main/java/ir/smsgaclient/service/BootReceiver.kt
package ir.smsgaclient.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

/**
 * BroadcastReceiver triggered on system boot (BOOT_COMPLETED).
 * Restarts the background foreground service if active.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Timber.i("Boot completed received, starting SmsForegroundService")
            SmsForegroundService.start(context)
        }
    }
}
