package com.hanas.addy.view.cardStackDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hanas.addy.model.PlayCardStack
import com.hanas.addy.view.cardStackList.PlayCardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CardStackDetailViewModel(
    private val playCardRepository: PlayCardRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val cardStackId = try {
        savedStateHandle.toRoute<CardStackDetail>().id
    } catch (e: Throwable) {
        throw IllegalArgumentException("Invalid card stack id", e)
    }

    val cardStack = MutableStateFlow<PlayCardStack?>(null)

    init {
        viewModelScope.launch {
            val x = playCardRepository.getPlayCardStackById(cardStackId)
            cardStack.value = x
        }
    }
}
