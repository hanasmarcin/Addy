package com.hanas.addy.view.playTable

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

sealed class CardWidth
data object ScreenWidth : CardWidth()
data class Width(val value: Dp) : CardWidth()


sealed class PlayingCardState(open val position: Int, open val amountInState: Int) {
    //    abstract fun targetOffset(): DpOffset
    abstract fun targetOffset(screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize): DpOffset
    abstract fun targetRotationZ(): Float
    abstract fun targetWidth(): CardWidth
    abstract fun targetRotationX(): Float
    abstract fun targetTransformOrigin(): Offset
    abstract fun targetIndexZ(): Float

    data class OnUnusedStack(
        override val position: Int,
        override val amountInState: Int = position,
    ) : PlayingCardState(position, amountInState) {
        override fun targetIndexZ() = position.toFloat()
        override fun targetOffset(screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize) = DpOffset(x = position.dp * 2, y = position.dp * -2)
        override fun targetRotationZ() = 0f
        override fun targetWidth() = Width(((1f + position * 0.005f) * 150).dp)
        override fun targetRotationX() = -180f
        override fun targetTransformOrigin() = Offset(1f, 0.5f)
    }

    data class OnCloseup(
        override val position: Int = Int.MAX_VALUE,
    ) : PlayingCardState(position, 1) {
        override fun targetIndexZ() = 1000f + position
        override fun targetOffset(screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize): DpOffset = DpOffset.Zero
        override fun targetRotationZ() = 0f
        override fun targetWidth() = ScreenWidth
        override fun targetRotationX() = 0f
        override fun targetTransformOrigin() = Offset(0.5f, 0.5f)

    }

    data class OnPlayStack(
        override val position: Int,
        override val amountInState: Int = position,
    ) : PlayingCardState(position, amountInState) {
        override fun targetIndexZ() = position.toFloat()
        override fun targetOffset(
            screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize
        ): DpOffset = DpOffset(x = position.dp * 2, y = position.dp * -2)

        override fun targetRotationZ() = 0f
        override fun targetWidth() = Width(((1f + position * 0.005f) * 150).dp)
        override fun targetRotationX() = 0f
        override fun targetTransformOrigin() = Offset(0f, 0.5f)
    }

    data class InHand(
        override val position: Int,
        override val amountInState: Int,
    ) : PlayingCardState(position, amountInState) {
        override fun targetIndexZ() = 1000.5f + position
        private val cardWidth = 100.dp
        override fun targetOffset(screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize) =
            DpOffset(
                x = (screenSizeInDp.width - cardWidth) /  (if (amountInState > 1) (amountInState - 1) else 1) * position,
                y = (screenSizeInDp.height - unscaledCardSizeInDp.height) / 2 + cardWidth
            )

        override fun targetRotationZ() = (position - amountInState / 2f) / amountInState * 5f
        override fun targetWidth() = Width(cardWidth)
        override fun targetRotationX() = 0f
        override fun targetTransformOrigin() = Offset(0f, 1f)
    }

    data class InTopOpponentHand(
        override val position: Int,
        override val amountInState: Int,
    ) : PlayingCardState(position, amountInState) {
        override fun targetIndexZ() = 1000.5f + (amountInState - position - 1)
        private val cardWidth = 100.dp
        override fun targetOffset(screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize) =
            DpOffset(
                x = (screenSizeInDp.width - cardWidth) / (if (amountInState > 1) (amountInState - 1) else 1) * position,
                y = -(screenSizeInDp.height - unscaledCardSizeInDp.height) / 2 + cardWidth
            )

        override fun targetRotationZ() = (position - amountInState / 2f) / amountInState * 5f
        override fun targetWidth() = Width(cardWidth)
        override fun targetRotationX() = -180f
        override fun targetTransformOrigin() = Offset(0f, 0f)
    }

}
