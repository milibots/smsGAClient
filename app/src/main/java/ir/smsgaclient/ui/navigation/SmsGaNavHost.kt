package ir.smsgaclient.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import ir.smsgaclient.ui.cards.CardsScreen
import ir.smsgaclient.ui.cards.CardsViewModel
import ir.smsgaclient.ui.common.SmsGaBottomBar
import ir.smsgaclient.ui.home.HomeScreen
import ir.smsgaclient.ui.home.HomeViewModel
import ir.smsgaclient.ui.onboarding.OnboardingScreen
import ir.smsgaclient.ui.settings.SettingsScreen
import ir.smsgaclient.ui.settings.SettingsViewModel
import ir.smsgaclient.ui.transactions.TransactionDetailScreen
import ir.smsgaclient.ui.transactions.TransactionsScreen
import ir.smsgaclient.ui.transactions.TransactionsViewModel

@Composable
fun SmsGaNavHost(
    navController: NavHostController,
    startDestination: String = NavRoutes.Home.route,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isBottomBarVisible = currentRoute in listOf(
        NavRoutes.Home.route,
        NavRoutes.Transactions.route,
        NavRoutes.Cards.route,
        NavRoutes.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                SmsGaBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(NavRoutes.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        containerColor = ir.smsgaclient.ui.theme.BackgroundMidnight,
        modifier = modifier
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(NavRoutes.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate(NavRoutes.Home.route) {
                            popUpTo(NavRoutes.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(NavRoutes.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToTransactions = { navController.navigate(NavRoutes.Transactions.route) },
                    onNavigateToDetail = { messageId ->
                        navController.navigate(NavRoutes.TransactionDetail.createRoute(messageId))
                    }
                )
            }

            composable(NavRoutes.Transactions.route) {
                val viewModel: TransactionsViewModel = hiltViewModel()
                TransactionsScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { messageId ->
                        navController.navigate(NavRoutes.TransactionDetail.createRoute(messageId))
                    }
                )
            }

            composable(NavRoutes.TransactionDetail.route) { backStackEntry ->
                val messageId = backStackEntry.arguments?.getString("messageId") ?: ""
                TransactionDetailScreen(
                    transaction = null,
                    rawSms = null,
                    parsedSms = null,
                    onBack = { navController.popBackStack() },
                    onRetryForward = {  }
                )
            }

            composable(NavRoutes.Cards.route) {
                val viewModel: CardsViewModel = hiltViewModel()
                CardsScreen(viewModel = viewModel)
            }

            composable(NavRoutes.Settings.route) {
                val viewModel: SettingsViewModel = hiltViewModel()
                SettingsScreen(
                    viewModel = viewModel,
                    onTestSms = {  },
                    onNavigateToPatternStudio = { navController.navigate(NavRoutes.PatternStudio.route) }
                )
            }

            composable(NavRoutes.PatternStudio.route) {
                val viewModel: ir.smsgaclient.ui.patterns.PatternStudioViewModel = hiltViewModel()
                ir.smsgaclient.ui.patterns.PatternStudioScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
