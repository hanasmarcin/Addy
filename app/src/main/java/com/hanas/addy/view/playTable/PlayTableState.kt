package com.hanas.addy.view.playTable

import com.hanas.addy.model.PlayingCardData

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
        val cards: List<PlayingCardData>,
        val availableSlots: Int = cards.size,
    )

    data class CardSlot(
        val card: PlayingCardData,
        val originSegment: PlayTableSegmentType,
        val positionWithinOriginSegment: Int
    )

    fun toCardStateMap() = mutableMapOf<PlayingCardData, PlayingCardState>().apply {
        if (closeUp != null) {
            set(closeUp.card, PlayingCardState.OnCloseup(closeUp.positionWithinOriginSegment))
        }
        unusedStack.cards.onEachIndexed { index, card ->
            if (card.isNotCloseUp()) set(card, PlayingCardState.OnUnusedStack(index, unusedStack.availableSlots))
        }
        playerHand.cards.onEachIndexed { index, card ->
            if (card.isNotCloseUp()) set(card, PlayingCardState.InHand(index, playerHand.availableSlots))
        }
        opponentHand.cards.onEachIndexed { index, card ->
            if (card.isNotCloseUp()) set(card, PlayingCardState.InTopOpponentHand(index, opponentHand.availableSlots))
        }
    }

    private fun PlayingCardData.isNotCloseUp() = this != closeUp?.card
}


fun <E> Collection<E>.indexOfOrNull(element: E): Int? = indexOf(element).takeIf { it != -1 }