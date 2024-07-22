package com.hanas.addy.view.playTable

import com.hanas.addy.model.PlayingCardData

enum class PlayTableSegmentType {
    UNUSED_STACK,
    PLAY_STACK,
    PLAYER_HAND,
    TOP_OPPONENT_HAND,
}

data class PlayTableSegment(
    val cards: List<PlayingCardData>,
    val availableSlots: Int = cards.size,
)

data class CloseUpCard(
    val card: PlayingCardData,
    val originSegment: PlayTableSegmentType,
    val positionWithinOriginSegment: Int
)

data class PlayTableState(
    val unusedStack: PlayTableSegment,
    val playStack: PlayTableSegment,
    val playerHand: PlayTableSegment,
    val topOpponentHand: PlayTableSegment,
    val closeUp: CloseUpCard? = null
) {
    fun toCardStateMap() = mutableMapOf<PlayingCardData, PlayingCardState>().apply {
        if (closeUp != null) {
            set(closeUp.card, PlayingCardState.OnCloseup(closeUp.positionWithinOriginSegment))
        }
        unusedStack.cards.onEachIndexed { index, card ->
            if (card.isNotCloseUp()) set(card, PlayingCardState.OnUnusedStack(index, unusedStack.availableSlots))
        }
        playStack.cards.onEachIndexed { index, card ->
            if (card.isNotCloseUp()) set(card, PlayingCardState.OnPlayStack(index, playStack.availableSlots))
        }
        playerHand.cards.onEachIndexed { index, card ->
            if (card.isNotCloseUp()) set(card, PlayingCardState.InHand(index, playerHand.availableSlots))
        }
        topOpponentHand.cards.onEachIndexed { index, card ->
            if (card.isNotCloseUp()) set(card, PlayingCardState.InTopOpponentHand(index, topOpponentHand.availableSlots))
        }
    }

    private fun PlayingCardData.isNotCloseUp() = this != closeUp?.card
}


fun <E> Collection<E>.indexOfOrNull(element: E): Int? = indexOf(element).takeIf { it != -1 }