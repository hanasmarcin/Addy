package com.hanas.addy.view.playTable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanas.addy.ui.samplePlayingCardStack
import com.hanas.addy.view.playTable.PlayTableSegmentType.PLAYER_HAND
import com.hanas.addy.view.playTable.PlayTableSegmentType.PLAY_STACK
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

private fun <T> List<T>.lastWithIndexOrNull(): Pair<T, Int>? {
    val last = lastOrNull() ?: return null
    return last to lastIndex
}

class PlayTableViewModel : ViewModel() {
    val lockInputFlow = MutableStateFlow(false)
    private val playingCards = samplePlayingCardStack.cards.take(15).shuffled()
    val playTableStateFlow = MutableStateFlow(
        PlayTableState(
            unusedStack = PlayTableSegment(playingCards.take(15)),
            playStack = PlayTableSegment(emptyList()),
            playerHand = PlayTableSegment(emptyList()),
            topOpponentHand = PlayTableSegment(emptyList()),
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
        lockInputFlow.value = true
        viewModelScope.launch {
            (1..5).forEach { _ ->
                delay(550)
                giveCardFromUnusedToPlayer()
            }
            (1..5).forEach { _ ->
                delay(550)
                giveCardFromUnusedToOpponent()
            }
            lockInputFlow.value = false
        }
    }

    fun onClickBox(clickBoxPosition: ClickBoxPosition) {
        lockInputFlow.value = true
        viewModelScope.launch {
            when (clickBoxPosition) {
                is ClickBoxPosition.Bottom -> {
                    closeUpTheCardFromPlayerHand(clickBoxPosition)
                }
                ClickBoxPosition.End -> {
                    if (tableState.closeUp != null) {
                        val result = moveCloseUpToNextCardInPlayerHand()
                        if (result) clearCloseUp()
                    } else {
                        giveCardFromUnusedToPlayer()
                    }
                }
                ClickBoxPosition.Start -> {
                    if (tableState.closeUp != null) {
                        val result = moveCloseUpToPreviousCardInPlayerHand()
                        if (result) clearCloseUp()
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
            lockInputFlow.value = false
        }
    }

    private fun clearCloseUp() {
        tableState = tableState.copy(closeUp = null)
    }

    private fun closeUpTheCardFromPlayStack(): Boolean {
        val (card, index) = tableState.playStack.cards.lastWithIndexOrNull() ?: return false
        tableState = tableState.copy(
            closeUp = CloseUpCard(card, PLAY_STACK, index)
        )
        return true
    }

    private fun closeUpTheCardFromPlayerHand(clickBoxPosition: ClickBoxPosition.Bottom) {
        val index = clickBoxPosition.index
        val card = tableState.playerHand.cards[index]
        tableState = tableState.copy(closeUp = CloseUpCard(card, PLAYER_HAND, index))
    }

    private fun moveCloseUpToNextCardInPlayerHand(): Boolean {
        tableState.closeUp?.let { closeUp ->
            if (closeUp.originSegment != PLAYER_HAND || closeUp.positionWithinOriginSegment == tableState.playerHand.availableSlots) return false
            val newPosition = closeUp.positionWithinOriginSegment + 1
            tableState = tableState.copy(closeUp = CloseUpCard(tableState.playerHand.cards[newPosition], PLAYER_HAND, newPosition))
            return true
        } ?: return false
    }

    private fun moveCloseUpToPreviousCardInPlayerHand(): Boolean {
        tableState.closeUp?.let { closeUp ->
            if (closeUp.originSegment != PLAYER_HAND || closeUp.positionWithinOriginSegment == 0) return false
            val newPosition = closeUp.positionWithinOriginSegment + 1
            tableState = tableState.copy(closeUp = CloseUpCard(tableState.playerHand.cards[newPosition], PLAYER_HAND, newPosition))
            return true
        } ?: return false
    }

    private suspend fun giveCardFromUnusedToPlayer(): Boolean {
        if (tableState.unusedStack.cards.isEmpty()) return false
        tableState = tableState.copy(
            playerHand = PlayTableSegment(tableState.playerHand.cards, tableState.playerHand.availableSlots + 1)
        )
        delay(550)
        tableState = tableState.copy(
            unusedStack = PlayTableSegment(tableState.unusedStack.cards.dropLast(1), tableState.unusedStack.availableSlots - 1),
            playerHand = PlayTableSegment(tableState.playerHand.cards + tableState.unusedStack.cards.last(), tableState.playerHand.availableSlots)
        )
        return true
    }

    private suspend fun giveCardFromUnusedToOpponent(): Boolean {
        if (tableState.unusedStack.cards.isEmpty()) return false
        tableState = tableState.copy(
            topOpponentHand = PlayTableSegment(tableState.topOpponentHand.cards, tableState.topOpponentHand.availableSlots + 1)
        )
        delay(550)
        tableState = tableState.copy(
            unusedStack = PlayTableSegment(tableState.unusedStack.cards.dropLast(1), tableState.unusedStack.availableSlots - 1),
            topOpponentHand = PlayTableSegment(
                tableState.topOpponentHand.cards + tableState.unusedStack.cards.last(),
                tableState.topOpponentHand.availableSlots
            )
        )
        return true
    }
}


