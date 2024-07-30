package com.hanas.addy.view.playTable.model

import com.hanas.addy.model.PlayCardData
import com.hanas.addy.view.playTable.view.uistate.PlayCardUiPlacement
import com.hanas.addy.view.playTable.view.uistate.PlayCardUiState

data class CardCollection(
    val cards: List<PlayCardData>,
    val size: Int = cards.size,
)

data class CardSlot(
    val card: PlayCardData,
    val contentState: PlayCardContentState,
)

data class PlayTableState(
    val deck: CardCollection,
    val playerHand: CardCollection,
    val opponentHand: CardCollection,
    val closeUp: CardSlot? = null,
    val playerBattleSlot: CardSlot? = null,
    val opponentBattleSlot: CardSlot? = null,
) {
    fun toCardStateMap() = mutableMapOf<Long, PlayCardUiState>().apply {
        playerBattleSlot?.let { slot ->
            this[slot.card.id] = PlayCardUiState(
                data = slot.card,
                placement = PlayCardUiPlacement.PlayerBattleSlot,
                contentState = slot.contentState
            )
        }
        opponentBattleSlot?.let { slot ->
            this[slot.card.id] = PlayCardUiState(
                data = slot.card,
                placement = PlayCardUiPlacement.OpponentBattleSlot,
                contentState = slot.contentState
            )
        }
        closeUp?.let { slot ->
            this[slot.card.id] = PlayCardUiState(
                data = slot.card,
                placement = PlayCardUiPlacement.CloseUp,
                contentState = slot.contentState
            )
        }
        deck.cards.filter { it.isNotInSlot() }.onEachIndexed { index, card ->
            if (card.isNotInSlot()) {
                this[card.id] = PlayCardUiState(card, PlayCardUiPlacement.Deck(index), BackFace.BackCover)
            }
        }
        playerHand.cards.onEachIndexed { index, card ->
            if (card.isNotInSlot()) {
                this[card.id] = PlayCardUiState(card, PlayCardUiPlacement.PlayerHand(index, playerHand.size), AttributesFace.CardPreview)
            }
        }
        opponentHand.cards.onEachIndexed { index, card ->
            if (card.isNotInSlot()) {
                this[card.id] = PlayCardUiState(card, PlayCardUiPlacement.OpponentHand(index, playerHand.size), BackFace.BackCover)
            }
        }
    }

    private fun PlayCardData.isNotInSlot() = this != closeUp?.card && this != playerBattleSlot?.card && this != opponentBattleSlot?.card
}