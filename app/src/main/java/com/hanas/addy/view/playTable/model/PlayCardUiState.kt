package com.hanas.addy.view.playTable.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin


sealed class PlayCardUiState(
    open val position: Int,
    open val amountInState: Int,
    open val content: PlayCardContentUiState,
    val clickOrigin: ClickOrigin?,
) {

    //    abstract fun targetOffset(): DpOffset
    abstract fun targetOffset(screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize): DpOffset
    abstract fun targetRotationZ(): Float
    abstract fun targetWidth(): Dp

    //    abstract fun targetRotationX(): Float
    abstract fun targetTransformOrigin(): Offset
    abstract fun targetIndexZ(): Float

    data class OnUnusedStack(
        override val position: Int,
        override val amountInState: Int = position,
    ) : PlayCardUiState(
        position = position,
        amountInState = amountInState,
        content = PlayCardContentUiState.BackCover,
        clickOrigin = null
    ) {
        override fun targetIndexZ() = position.toFloat()
        override fun targetOffset(screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize) = DpOffset(x = position.dp * 2, y = position.dp * -2)
        override fun targetRotationZ() = 0f
        override fun targetWidth() = ((1f + position * 0.005f) * 100).dp
        override fun targetTransformOrigin() = Offset(1f, 0.5f)
    }

    data class OnCloseUp(
        override val position: Int = Int.MAX_VALUE,
        override val content: PlayCardContentUiState,
    ) : PlayCardUiState(
        position = position,
        amountInState = 1,
        content = content,
        clickOrigin = ClickOrigin.CLOSE_UP
    ) {

        override fun targetIndexZ() = 1000f + position
        override fun targetOffset(screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize): DpOffset = DpOffset.Zero
        override fun targetRotationZ() = 0f
        override fun targetWidth() = Dp.Infinity
        override fun targetTransformOrigin() = Offset(0.5f, 0.5f)
    }

    data class OnBattleSlotForPlayer(
        override val position: Int,
        override val amountInState: Int = position,
        override val content: PlayCardContentUiState,
    ) : PlayCardUiState(
        position = position,
        amountInState = amountInState,
        content = content,
        clickOrigin = ClickOrigin.PLAYER_BATTLE_SLOT.takeIf { content is PlayCardContentUiState.AttributesDisplay },
    ) {
        override fun targetIndexZ() = position.toFloat()
        override fun targetOffset(
            screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize
        ): DpOffset = DpOffset(x = position.dp * 2, y = position.dp * -2)

        override fun targetRotationZ() = 0f
        override fun targetWidth() = ((1f + position * 0.005f) * 100).dp
        override fun targetTransformOrigin() = Offset(0f, 0.2f)
    }

    data class OnBattleSlotForOpponent(
        override val position: Int,
        override val amountInState: Int = position,
        override val content: PlayCardContentUiState,
    ) : PlayCardUiState(
        position = position,
        amountInState = amountInState,
        content = content,
        clickOrigin = ClickOrigin.OPPONENT_BATTLE_SLOT.takeIf { content is PlayCardContentUiState.AttributesDisplay },
    ) {
        override fun targetIndexZ() = position.toFloat()
        override fun targetOffset(
            screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize
        ): DpOffset = DpOffset(x = position.dp * 2, y = position.dp * -2)

        override fun targetRotationZ() = 0f
        override fun targetWidth() = (((1f + position * 0.005f) * 100).dp)
        override fun targetTransformOrigin() = Offset(0f, 0f)
    }

    data class InHand(
        override val position: Int,
        override val amountInState: Int,
        val isClickAvailable: Boolean,
    ) : PlayCardUiState(
        position = position,
        amountInState = amountInState,
        content = PlayCardContentUiState.AttributesDisplay.Initial,
        clickOrigin = ClickOrigin.PLAYER_HAND.takeIf { isClickAvailable },
    ) {
        override fun targetIndexZ() = 1000.5f + position
        private val cardWidth = 100.dp
        override fun targetOffset(screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize) =
            DpOffset(
                x = (screenSizeInDp.width - cardWidth) / (if (amountInState > 1) (amountInState - 1) else 1) * position,
                y = (screenSizeInDp.height - unscaledCardSizeInDp.height) / 2 + cardWidth
            )

        override fun targetRotationZ() = (position - amountInState / 2f) / amountInState * 5f
        override fun targetWidth() = (cardWidth)
        override fun targetTransformOrigin() = Offset(0f, 1f)
    }

    data class InTopOpponentHand(
        override val position: Int,
        override val amountInState: Int,
    ) : PlayCardUiState(
        position = position,
        amountInState = amountInState,
        content = PlayCardContentUiState.BackCover,
        clickOrigin = null,
    ) {
        override fun targetIndexZ() = 1000.5f + (amountInState - position - 1)
        private val cardWidth = 100.dp
        override fun targetOffset(screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize) =
            DpOffset(
                x = (screenSizeInDp.width - cardWidth) / (if (amountInState > 1) (amountInState - 1) else 1) * position,
                y = -(screenSizeInDp.height - unscaledCardSizeInDp.height) / 2 + cardWidth
            )

        override fun targetRotationZ() = (position - amountInState / 2f) / amountInState * 5f
        override fun targetWidth() = (cardWidth)
        override fun targetTransformOrigin() = Offset(0f, 0f)
    }

//    abstract fun targetRotationY(): Float
}
