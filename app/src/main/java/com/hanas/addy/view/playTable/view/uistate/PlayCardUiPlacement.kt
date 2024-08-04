package com.hanas.addy.view.playTable.view.uistate

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

sealed class PlayCardUiPlacement {
    // Abstract functions to calculate visual properties
    abstract fun targetOffset(screenSize: DpSize, cardSize: DpSize): DpOffset
    abstract fun targetRotationZ(): Float
    abstract fun targetWidth(): Dp
    abstract fun targetZIndex(): Float

    // Card is in the unused deck
    data class Deck(val indexInDeck: Int) : PlayCardUiPlacement() {
        override fun targetZIndex() = indexInDeck.toFloat()

        override fun targetOffset(screenSize: DpSize, cardSize: DpSize) = DpOffset(
            x = screenSize.width * 0.2f + indexInDeck.dp * 2,
            y = indexInDeck.dp * -2
        )

        override fun targetRotationZ() = 0f
        override fun targetWidth() = ((1f + indexInDeck * 0.005f) * 100).dp
    }

    // Card is in the close-up view area
    data object CloseUp : PlayCardUiPlacement() {
        override fun targetZIndex() = 1000f
        override fun targetOffset(screenSize: DpSize, cardSize: DpSize): DpOffset = DpOffset.Zero
        override fun targetRotationZ() = 0f
        override fun targetWidth() = Dp.Infinity
    }

    // Card is in the player's battle slot
    data object PlayerBattleSlot : PlayCardUiPlacement() {
        override fun targetZIndex() = 0f
        override fun targetOffset(screenSize: DpSize, cardSize: DpSize): DpOffset =
            DpOffset(-screenSize.width * 0.2f, targetWidth())

        override fun targetRotationZ() = 0f
        override fun targetWidth() = 100.dp
    }

    // Card is in the top opponent's battle slot
    data object OpponentBattleSlot : PlayCardUiPlacement() {
        override fun targetZIndex() = 0f
        override fun targetOffset(screenSize: DpSize, cardSize: DpSize): DpOffset =
            DpOffset(-screenSize.width * 0.2f, -targetWidth())

        override fun targetRotationZ() = 0f
        override fun targetWidth() = 100.dp
    }

    // Card is in the player's hand
    data class PlayerHand(
        val indexInHand: Int,
        val handSize: Int
    ) : PlayCardUiPlacement() {
        override fun targetZIndex() = 1000.5f + indexInHand
        private val cardWidth = 100.dp

        override fun targetOffset(screenSize: DpSize, cardSize: DpSize) =
            DpOffset(
                x = (screenSize.width - cardWidth) / (if (handSize > 1) (handSize - 1) else 1) * indexInHand - (screenSize.width - cardWidth) / 2,
                y = screenSize.height / 2
            )

        override fun targetRotationZ() = (indexInHand - handSize / 2f) / handSize * 5f
        override fun targetWidth() = cardWidth
    }

    // Card is in the top opponent's hand
    data class OpponentHand(
        val indexInHand: Int,
        val handSize: Int
    ) : PlayCardUiPlacement() {
        override fun targetZIndex() = -500f + (handSize - indexInHand - 1)
        private val cardWidth = 100.dp

        override fun targetOffset(screenSize: DpSize, cardSize: DpSize) =
            DpOffset(
                x = (screenSize.width - cardWidth) / (if (handSize > 1) (handSize - 1) else 1) * indexInHand - (screenSize.width - cardWidth) / 2,
                y = -screenSize.height / 2
            )

        override fun targetRotationZ() = (indexInHand - handSize / 2f) / handSize * 5f
        override fun targetWidth() = cardWidth
    }
}