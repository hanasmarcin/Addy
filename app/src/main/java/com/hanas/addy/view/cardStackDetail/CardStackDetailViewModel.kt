package com.hanas.addy.view.cardStackDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hanas.addy.model.PlayCardStack
import com.hanas.addy.view.cardStackList.CardStackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CardStackDetailViewModel(
    private val cardStackRepository: CardStackRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val cardStackId by lazy { savedStateHandle.toRoute<CardStackDetail>().id }

    private val _cardStack = MutableStateFlow<PlayCardStack?>(null)
    val cardStack: StateFlow<PlayCardStack?>
        get() = _cardStack

    init {
        viewModelScope.launch {
            val x = cardStackRepository.getPlayCardStackById(cardStackId)
            _cardStack.value = x
        }
    }
}
