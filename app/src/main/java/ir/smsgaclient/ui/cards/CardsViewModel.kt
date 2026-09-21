// app/src/main/java/ir/smsgaclient/ui/cards/CardsViewModel.kt
package ir.smsgaclient.ui.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.smsgaclient.data.db.dao.CardDao
import ir.smsgaclient.data.db.entity.CardEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CardWithUsage(
    val card: CardEntity,
    val usageRial: Long = 0L,
    val usagePercent: Float = 0f
)

data class CardsUiState(
    val cards: List<CardWithUsage> = emptyList(),
    val isAddCardDialogOpen: Boolean = false
)

@HiltViewModel
class CardsViewModel @Inject constructor(
    private val cardDao: CardDao
) : ViewModel() {

    private val isDialogOpen = MutableStateFlow(false)

    val uiState: StateFlow<CardsUiState> = cardDao.getAllCards().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    ).let { cardsFlow ->
        MutableStateFlow(CardsUiState())
    }

    fun openAddCardDialog() {
        isDialogOpen.value = true
    }

    fun closeAddCardDialog() {
        isDialogOpen.value = false
    }

    fun addCard(bankId: String, last4: String, holderName: String, dailyLimitRial: Long) {
        viewModelScope.launch {
            val card = CardEntity(
                bankId = bankId,
                last4 = last4.takeLast(4),
                holderName = holderName,
                dailyLimitRial = dailyLimitRial,
                isActive = true,
                priority = 0
            )
            cardDao.insert(card)
            closeAddCardDialog()
        }
    }
}
