// app/src/main/java/ir/smsgaclient/ui/home/HomeViewModel.kt
package ir.smsgaclient.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.smsgaclient.data.db.dao.TransactionDao
import ir.smsgaclient.data.db.entity.TransactionEntity
import ir.smsgaclient.data.prefs.SecurePrefs
import ir.smsgaclient.ui.common.BridgeStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class HomeUiState(
    val todaySalesToman: Long = 0L,
    val todayTransactionsCount: Int = 0,
    val pendingQueueCount: Int = 0,
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val bridgeStatus: BridgeStatus = BridgeStatus.NOT_CONFIGURED,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val securePrefs: SecurePrefs
) : ViewModel() {

    private val isRefreshingFlow = MutableStateFlow(false)

    private fun getStartOfDayTehran(): Long {
        val tehranZone = ZoneId.of("Asia/Tehran")
        return LocalDate.now(tehranZone).atStartOfDay(tehranZone).toInstant().toEpochMilli()
    }

    val uiState: StateFlow<HomeUiState> = combine(
        transactionDao.getTodayTotalSalesRial(getStartOfDayTehran()),
        transactionDao.getTodayTransactionCount(getStartOfDayTehran()),
        transactionDao.getPendingQueueCount(),
        transactionDao.getRecentTransactions(3),
        isRefreshingFlow
    ) { salesRial, count, queue, recents, refreshing ->
        val isConfigured = !securePrefs.webhookUrl.isNullOrEmpty() && securePrefs.isPaired
        val status = when {
            !isConfigured -> BridgeStatus.NOT_CONFIGURED
            queue > 0 -> BridgeStatus.CONNECTED_QUEUED
            else -> BridgeStatus.CONNECTED_FORWARDING
        }

        HomeUiState(
            todaySalesToman = (salesRial ?: 0L) / 10L,
            todayTransactionsCount = count,
            pendingQueueCount = queue,
            recentTransactions = recents,
            bridgeStatus = status,
            isRefreshing = refreshing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun refresh() {
        // Silent flow refresh triggered automatically by Room
    }
}
