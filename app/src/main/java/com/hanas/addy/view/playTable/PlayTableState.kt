package com.hanas.addy.view.playTable

import com.hanas.addy.model.PlayingCard

enum class PlayTableSegmentType {
    UNUSED_STACK,
    PLAY_STACK,
    PLAYER_HAND,
    TOP_OPPONENT_HAND,
}

data class PlayTableSegment(
    val cards: List<PlayingCard>,
    val availableSlots: Int = cards.size,
)

data class CloseUpCard(
    val card: PlayingCard,
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
    fun toCardStateMap() = mutableMapOf<PlayingCard, CardState>().apply {
        if (closeUp != null) {
            set(closeUp.card, CardState.OnCloseup(closeUp.positionWithinOriginSegment))
        }
        unusedStack.cards.onEachIndexed { index, card ->
            if (card.isNotCloseUp()) set(card, CardState.OnUnusedStack(index, unusedStack.availableSlots))
        }
        playStack.cards.onEachIndexed { index, card ->
            if (card.isNotCloseUp()) set(card, CardState.OnPlayStack(index, playStack.availableSlots))
        }
        playerHand.cards.onEachIndexed { index, card ->
            if (card.isNotCloseUp()) set(card, CardState.InHand(index, playerHand.availableSlots))
        }
        topOpponentHand.cards.onEachIndexed { index, card ->
            if (card.isNotCloseUp()) set(card, CardState.InTopOpponentHand(index, topOpponentHand.availableSlots))
        }
    }

    private fun PlayingCard.isNotCloseUp() = this != closeUp?.card
}


fun <E> Collection<E>.indexOfOrNull(element: E): Int? = indexOf(element).takeIf { it != -1 }