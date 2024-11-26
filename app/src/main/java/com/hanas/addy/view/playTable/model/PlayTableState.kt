package com.hanas.addy.view.playTable.model

import com.hanas.addy.model.PlayCardData
import com.hanas.addy.view.playTable.PlayerState
import com.hanas.addy.view.playTable.PositionOnTable
import com.hanas.addy.view.playTable.PositionOnTable.BOTTOM
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
    val players: Map<PositionOnTable, PlayerState>,
    val deck: CardCollection,
    val closeUp: CardSlot? = null,
    val inHands: Map<PositionOnTable, CardCollection>,
    val battleSlots: Map<PositionOnTable, CardSlot?> = PositionOnTable.entries.associateWith { null },
) {
    fun toCardStateMap() = mutableMapOf<Long, PlayCardUiState>().apply {
        battleSlots.onEach { (position, slot) ->
            if (slot != null) {
                this[slot.card.id] = PlayCardUiState(
                    data = slot.card,
                    placement = PlayCardUiPlacement.BattleSlot(position),
                    contentState = slot.contentState
                )
            }
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
        inHands.onEach { (position, hand) ->
            val contentState = if (position == BOTTOM) AttributesFace.StaticPreview else BackFace.BackCover
            hand.cards.onEachIndexed { index, card ->
                if (card.isNotInSlot()) {
                    this[card.id] = PlayCardUiState(card, PlayCardUiPlacement.InHand(position, index, hand.size), contentState)
                }
            }
        }
    }

    private fun PlayCardData.isNotInSlot() = this != closeUp?.card && this !in battleSlots.values.map { it?.card }
}