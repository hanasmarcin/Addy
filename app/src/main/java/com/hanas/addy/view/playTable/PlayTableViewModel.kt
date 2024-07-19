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

    private var tableState: PlayTableState
        get() = playTableStateFlow.value
        set(value) {
            playTableStateFlow.value = value
        }


    init {
        dealCardsAtStart()
    }

    fun dealCardsAtStart() {
        viewModelScope.launch {
            (1..5).forEach { _ ->
                delay(550)
                giveCardFromUnusedToPlayer()
            }
            (1..5).forEach { _ ->
                delay(550)
                giveCardFromUnusedToOpponent()
            }
        }
    }

    fun onClickBox(clickBoxPosition: ClickBoxPosition) {
        viewModelScope.launch {
            when (clickBoxPosition) {
                is ClickBoxPosition.Bottom -> {
                    closeUpTheCardFromPlayerHand(clickBoxPosition)
                }
                ClickBoxPosition.End -> {
                    if (tableState.closeUp != null) {
                        moveCloseUpToNextCard()
                    } else {
                        giveCardFromUnusedToPlayer()
                    }
                }
                ClickBoxPosition.Start -> {
                    if (tableState.closeUp != null) {
                        moveCloseUpToPreviousCardInPlayerHand()
                    } else {
                        closeUpTheCardFromPlayStack()
                    }
                }
                ClickBoxPosition.Top -> {
                    if (tableState.closeUp != null) {
                        tableState = tableState.copy(closeUp = null)
                    }
                }
            }
        }
    }

    private fun closeUpTheCardFromPlayStack(): Boolean {
        val card = tableState.playStack.lastOrNull() ?: return false
        tableState = tableState.copy(closeUp = card)
        return true
    }

    private fun closeUpTheCardFromPlayerHand(clickBoxPosition: ClickBoxPosition.Bottom) {
        val index = clickBoxPosition.index
        val card = tableState.playerHand[index]
        tableState = tableState.copy(closeUp = card)
    }

    private fun moveCloseUpToNextCard(): Boolean {
        val closeUpIndex = tableState.playerHand.indexOf(tableState.closeUp)
        if (closeUpIndex == -1 || closeUpIndex == tableState.playerHand.lastIndex) return false
        tableState = tableState.copy(closeUp = tableState.playerHand[closeUpIndex + 1])
        return true
    }

    private fun moveCloseUpToPreviousCardInPlayerHand(): Boolean {
        val closeUpIndex = tableState.playerHand.indexOf(tableState.closeUp)
        if (closeUpIndex == -1 || closeUpIndex == 0) return false
        tableState = tableState.copy(closeUp = tableState.playerHand[closeUpIndex - 1])
        return true
    }

    private fun giveCardFromUnusedToPlayer() {
        if (tableState.unusedStack.isEmpty()) return
        tableState = tableState.let {
            it.copy(
                unusedStack = it.unusedStack.dropLast(1),
                playerHand = it.playerHand + it.unusedStack.last()
            )
        }
    }

    private fun giveCardFromUnusedToOpponent() {
        if (tableState.unusedStack.isEmpty()) return
        tableState = tableState.let {
            it.copy(
                unusedStack = it.unusedStack.dropLast(1),
                topOpponentHand = it.topOpponentHand + it.unusedStack.last()
            )
        }
    }
}


