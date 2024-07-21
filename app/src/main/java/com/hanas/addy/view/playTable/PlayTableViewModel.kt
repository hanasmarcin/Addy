package com.hanas.addy.view.playTable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanas.addy.model.Answer
import com.hanas.addy.repository.samplePlayingCardStack
import com.hanas.addy.view.playTable.PlayTableSegmentType.PLAYER_HAND
import com.hanas.addy.view.playTable.PlayTableState.CardSlot
import com.hanas.addy.view.playTable.PlayTableState.Segment
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
            unusedStack = Segment(playingCards.take(15)),
            playerHand = Segment(emptyList()),
            opponentHand = Segment(emptyList()),
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

    fun onClickCard(cardState: PlayingCardState) {
        lockInputFlow.value = true
        viewModelScope.launch {
            when (cardState) {
//                is ClickBoxPosition.Bottom -> {
//                }
//                ClickBoxPosition.End -> {
//                    if (tableState.closeUp != null) {
//                        val result = moveCloseUpToNextCardInPlayerHand()
//                        if (result.not()) clearCloseUp()
//                    } else {
//
//                    }
//                }
//                ClickBoxPosition.Start -> {
//                    if (tableState.closeUp != null) {
//                        val result = moveCloseUpToPreviousCardInPlayerHand()
//                        if (result.not()) clearCloseUp()
//                    } else {
//                        closeUpTheCardFromPlayStack()
//                    }
//                }
//                ClickBoxPosition.Top -> {
//                    if (tableState.closeUp != null) {
//                        tableState = tableState.copy(closeUp = null)
//                    }
//                }
                is PlayingCardState.InHand -> {
                    closeUpTheCardFromPlayerHand(cardState.position)

                }
                is PlayingCardState.InTopOpponentHand -> {
                    /* Do nothing */
                }
                is PlayingCardState.OnCloseup -> {
                    clearCloseUp()
                }
                is PlayingCardState.OnBattleSlotForPlayer -> {
                    closeUpTheCardFromBattleSlot()
                }
                is PlayingCardState.OnBattleSlotForOpponent -> {
                }
                is PlayingCardState.OnUnusedStack -> {
                    giveCardFromUnusedToPlayer(cardState.position)
                }
            }
            lockInputFlow.value = false
        }
    }

    private fun clearCloseUp() {
        tableState = tableState.copy(closeUp = null)
    }

    private fun closeUpTheCardFromBattleSlot(): Boolean {
        val card = tableState.playerBattleSlot ?: return false
        tableState = tableState.copy(
            closeUp = card
        )
        return true
    }

    private fun closeUpTheCardFromPlayerHand(position: Int) {
        val card = tableState.playerHand.cards[position]
        tableState = tableState.copy(closeUp = CardSlot(card, PLAYER_HAND, position))
    }

    private fun moveCloseUpToNextCardInPlayerHand(): Boolean {
        tableState.closeUp?.let { closeUp ->
            if (closeUp.originSegment != PLAYER_HAND || closeUp.positionWithinOriginSegment == tableState.playerHand.availableSlots - 1) return false
            val newPosition = closeUp.positionWithinOriginSegment + 1
            tableState = tableState.copy(closeUp = CardSlot(tableState.playerHand.cards[newPosition], PLAYER_HAND, newPosition))
            return true
        } ?: return false
    }

    private fun moveCloseUpToPreviousCardInPlayerHand(): Boolean {
        tableState.closeUp?.let { closeUp ->
            if (closeUp.originSegment != PLAYER_HAND || closeUp.positionWithinOriginSegment == 0) return false
            val newPosition = closeUp.positionWithinOriginSegment - 1
            tableState = tableState.copy(closeUp = CardSlot(tableState.playerHand.cards[newPosition], PLAYER_HAND, newPosition))
            return true
        } ?: return false
    }

    private suspend fun giveCardFromUnusedToPlayer(position: Int = tableState.unusedStack.cards.lastIndex) {
        val targetCard = tableState.unusedStack.cards[position]
        tableState = tableState.copy(
            playerHand = Segment(tableState.playerHand.cards, tableState.playerHand.availableSlots + 1)
        )
        delay(550)
        tableState = tableState.copy(
            unusedStack = Segment(tableState.unusedStack.cards.dropAt(position), tableState.unusedStack.availableSlots - 1),
            playerHand = Segment(tableState.playerHand.cards + targetCard, tableState.playerHand.availableSlots)
        )
    }

    private suspend fun giveCardFromUnusedToOpponent(): Boolean {
        if (tableState.unusedStack.cards.isEmpty()) return false
        tableState = tableState.copy(
            opponentHand = Segment(tableState.opponentHand.cards, tableState.opponentHand.availableSlots + 1)
        )
        delay(550)
        tableState = tableState.copy(
            unusedStack = Segment(tableState.unusedStack.cards.dropLast(1), tableState.unusedStack.availableSlots - 1),
            opponentHand = Segment(
                tableState.opponentHand.cards + tableState.unusedStack.cards.last(),
                tableState.opponentHand.availableSlots
            )
        )
        return true
    }

    fun onClickAwayFromCloseUp() {
        clearCloseUp()
    }

    fun onSelectAnswer(playingCardState: PlayingCardState, answer: Answer) {

    }
}

private fun <E> List<E>.dropAt(position: Int) = take(position) + drop(position + 1)

