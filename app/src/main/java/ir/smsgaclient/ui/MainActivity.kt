package ir.smsgaclient.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.smsgaclient.ui.navigation.SmsGaNavHost
import ir.smsgaclient.ui.theme.SmsGaTheme

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
