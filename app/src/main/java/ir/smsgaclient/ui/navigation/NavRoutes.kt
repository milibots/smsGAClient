// app/src/main/java/ir/smsgaclient/ui/navigation/NavRoutes.kt
package ir.smsgaclient.ui.navigation

/**
 * Type-safe navigation routes for smsGAClient Cockpit.
 */
sealed class NavRoutes(val route: String) {
    data object Onboarding : NavRoutes("onboarding")
    data object Pair : NavRoutes("pair")
    data object Home : NavRoutes("home")
    data object Transactions : NavRoutes("transactions")
    data object TransactionDetail : NavRoutes("transaction_detail/{messageId}") {
        fun createRoute(messageId: String): String = "transaction_detail/$messageId"
    }
    data object Cards : NavRoutes("cards")
    data object Settings : NavRoutes("settings")
}
