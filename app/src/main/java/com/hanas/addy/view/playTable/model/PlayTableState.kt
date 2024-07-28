package com.hanas.addy.view.playTable.model

import com.hanas.addy.model.PlayCardData

enum class PlayTableSegmentType {
    UNUSED_STACK,
    PLAYER_HAND,
    TOP_OPPONENT_HAND,
}

data class PlayTableState(
    val unusedStack: Segment,
    val playerHand: Segment,
    val opponentHand: Segment,
    val closeUp: CardSlot? = null,
    val playerBattleSlot: CardSlot? = null,
    val opponentBattleSlot: CardSlot? = null,
) {
    data class Segment(
        val cards: List<PlayCardData>,
        val availableSlots: Int = cards.size,
    )

    data class CardSlot(
        val card: PlayCardData,
        val originSegment: PlayTableSegmentType,
        val positionWithinOriginSegment: Int,
        val contentState: PlayCardContentUiState = PlayCardContentUiState.AttributesDisplay.Initial,
    )

    fun toCardStateMap() = mutableMapOf<PlayCardData, PlayCardUiState>().apply {
        if (playerBattleSlot != null) {
            set(playerBattleSlot.card, PlayCardUiState.OnBattleSlotForPlayer(0, 1, playerBattleSlot.contentState))
        }
        if (opponentBattleSlot != null) {
            set(opponentBattleSlot.card, PlayCardUiState.OnBattleSlotForOpponent(0, 1, opponentBattleSlot.contentState))
        }
        if (closeUp != null) {
            set(closeUp.card, PlayCardUiState.OnCloseUp(closeUp.positionWithinOriginSegment, closeUp.contentState))
        }
        unusedStack.cards.onEachIndexed { index, card ->
            if (card.isNotInSlot()) set(card, PlayCardUiState.OnUnusedStack(index, unusedStack.availableSlots))
        }
        playerHand.cards.onEachIndexed { index, card ->
            val isClickAvailable = closeUp == null || closeUp.contentState is PlayCardContentUiState.AttributesDisplay
            if (card.isNotInSlot()) set(card, PlayCardUiState.InHand(index, playerHand.availableSlots, isClickAvailable))
        }
        opponentHand.cards.onEachIndexed { index, card ->
            if (card.isNotInSlot()) set(card, PlayCardUiState.InTopOpponentHand(index, opponentHand.availableSlots))
        }
    }

    private fun PlayCardData.isNotInSlot() = this != closeUp?.card && this != playerBattleSlot?.card && this != opponentBattleSlot?.card
}


fun <E> Collection<E>.indexOfOrNull(element: E): Int? = indexOf(element).takeIf { it != -1 }