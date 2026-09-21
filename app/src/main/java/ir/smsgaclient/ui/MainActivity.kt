// app/src/main/java/ir/smsgaclient/ui/MainActivity.kt
package ir.smsgaclient.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.smsgaclient.ui.navigation.SmsGaNavHost
import ir.smsgaclient.ui.theme.SmsGaTheme

/**
 * Main entry point activity for smsGAClient Cockpit.
 * Hosts the Jetpack Compose navigation graph and responds to deep links.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmsGaTheme {
                val navController = rememberNavController()
                SmsGaNavHost(navController = navController)
            }
        }
    }
}
