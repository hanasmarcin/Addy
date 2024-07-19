package com.hanas.addy.view.playTable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanas.addy.ui.samplePlayingCardStack
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PlayTableViewModel : ViewModel() {
    val playingCards = samplePlayingCardStack.cards.take(15).shuffled()
    val playTableStateFlow = MutableStateFlow(
        PlayTableState(
            unusedStack = playingCards.take(15),
            playStack = emptyList(),
            playerHand = emptyList(),
            topOpponentHand = emptyList()
        )
    )

    init {
        dealCardsAtStart()
    }

    fun dealCardsAtStart() {
        viewModelScope.launch {
            (1..5).forEach { _ ->
                delay(1100)
                giveCardFromUnusedToPlayer()
            }
            (1..5).forEach { _ ->
                delay(1100)
                giveCardFromUnusedToOpponent()
            }
        }
    }

    private fun giveCardFromUnusedToPlayer() {
        playTableStateFlow.value = playTableStateFlow.value.let {
            it.copy(
                unusedStack = it.unusedStack.dropLast(1),
                playerHand = it.playerHand + it.unusedStack.last()
            )
        }
    }

    private fun giveCardFromUnusedToOpponent() {
        playTableStateFlow.value = playTableStateFlow.value.let {
            it.copy(
                unusedStack = it.unusedStack.dropLast(1),
                topOpponentHand = it.topOpponentHand + it.unusedStack.last()
            )
        }
    }

    fun onClickBox(clickBoxPosition: ClickBoxPosition) {
        viewModelScope.launch {
            when (clickBoxPosition) {
                is ClickBoxPosition.Bottom -> {
                    val index = clickBoxPosition.index
                    val card = playTableStateFlow.value.playerHand[index]
                    delay(500)
                    playTableStateFlow.value = playTableStateFlow.value.copy(closeUp = card)
                }
                ClickBoxPosition.End -> {
                    giveCardFromUnusedToPlayer()
                }
                ClickBoxPosition.Start -> {}
                ClickBoxPosition.Top -> {}
            }
        }
    }
}


