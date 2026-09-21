package ir.smsgaclient.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.smsgaclient.data.db.dao.TransactionDao
import ir.smsgaclient.data.db.entity.TransactionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class DateFilter(val titleFa: String) {
    TODAY("امروز"),
    WEEK("هفته"),
    MONTH("ماه"),
    ALL("همه")
}

data class TransactionsUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val filteredTransactions: List<TransactionEntity> = emptyList(),
    val selectedFilter: DateFilter = DateFilter.ALL,
    val searchQuery: String = ""
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    transactionDao: TransactionDao
) : ViewModel() {

    private val selectedFilter = MutableStateFlow(DateFilter.ALL)
    private val searchQuery = MutableStateFlow("")

    val uiState: StateFlow<TransactionsUiState> = combine(
        transactionDao.getAllTransactionsFlow(),
        selectedFilter,
        searchQuery
    ) { allTx, filter, query ->
        val filtered = allTx.filter { tx ->
            val matchesQuery = query.isEmpty() ||
                    tx.bankId.contains(query, ignoreCase = true) ||
                    (tx.cardLast4?.contains(query) == true) ||
                    tx.amountRial.toString().contains(query)

            matchesQuery
        }

        TransactionsUiState(
            transactions = allTx,
            filteredTransactions = filtered,
            selectedFilter = filter,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionsUiState()
    )

    fun onFilterSelected(filter: DateFilter) {
        selectedFilter.value = filter
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }
}
