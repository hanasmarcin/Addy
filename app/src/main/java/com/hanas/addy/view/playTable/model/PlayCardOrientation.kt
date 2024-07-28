package com.hanas.addy.view.playTable.model

import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.TwoWayConverter
import com.hanas.addy.view.playTable.model.PlayCardContentUiType.ATTRIBUTES
import com.hanas.addy.view.playTable.model.PlayCardContentUiType.BACK_COVER
import com.hanas.addy.view.playTable.model.PlayCardContentUiType.QUESTION

data class PlayCardOrientation(
    val rotationX: Float,
    val rotationY: Float,
    val contentType: PlayCardContentUiType,
) {
    companion object {
        val ToVector: TwoWayConverter<PlayCardOrientation, AnimationVector2D> = TwoWayConverter(
            convertToVector = { state ->
                AnimationVector2D(state.rotationX, state.rotationY)
            },
            convertFromVector = { vector ->
                val rotationX = vector.v1
                val rotationY = vector.v2
                val contentToShow = when {
                    rotationY in -180f..-90f && rotationX in -90f..0f -> QUESTION
                    rotationY in -90f..0f && rotationX in -90f..0f -> ATTRIBUTES
                    else -> BACK_COVER
                }
                PlayCardOrientation(rotationX, rotationY, contentToShow)
            }
        )
    }
}
