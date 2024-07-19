package com.hanas.addy.view.playTable

import com.hanas.addy.model.PlayingCard

data class PlayTableState(
    val unusedStack: List<PlayingCard>,
    val playStack: List<PlayingCard>,
    val playerHand: List<PlayingCard>,
    val topOpponentHand: List<PlayingCard>,
    val closeUp: PlayingCard? = null
) {
    fun toCardStateMap() = mutableMapOf<String, CardState>().apply {
        if (closeUp != null) set(closeUp.title, CardState.OnCloseup(position = unusedStack.indexOf(closeUp), closeUp))
        unusedStack.onEachIndexed { index, card ->
            if (card != closeUp) set(card.title, CardState.OnUnusedStack(index, unusedStack.size, card))
        }
        playStack.onEachIndexed { index, card ->
            if (card != closeUp) set(card.title, CardState.OnPlayStack(index, playStack.size, card))
        }
        playerHand.onEachIndexed { index, card ->
            if (card != closeUp) set(card.title, CardState.InHand(index, playerHand.size, card))
        }
        topOpponentHand.onEachIndexed { index, card ->
            if (card != closeUp) set(card.title, CardState.InTopOpponentHand(index, topOpponentHand.size, card))
        }
    }
}

fun <E> Collection<E>.indexOfOrNull(element: E): Int? = indexOf(element).takeIf { it != -1 }