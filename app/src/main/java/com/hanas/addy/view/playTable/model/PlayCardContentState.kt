package com.hanas.addy.view.playTable.model

import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.TwoWayConverter
import com.hanas.addy.model.Answer
import com.hanas.addy.model.Attribute

sealed class PlayCardContentState(
    open val isClickable: Boolean,
    val rotation: CardRotation
)

sealed class BackFace : PlayCardContentState(
    isClickable = false,
    rotation = CardRotation.Back()
) {
    data object BackCover : BackFace()
    data object OpponentAnswering : BackFace()
    data object OpponentWaitingForAttributeBattle : BackFace()
}

sealed class AttributesFace : PlayCardContentState(
    isClickable = true,
    rotation = CardRotation.FrontDefault() // Assuming attributes are displayed in the default front rotation
) {
    data object CardPreview : AttributesFace()
    data class AddingBoost(val boostForRed: Int, val boostForGreen: Int, val boostForBlue: Int) : AttributesFace()
    data object WaitingForAttributeBattle : AttributesFace()
    data class ChooseActiveAttribute(val selectedAttribute: Attribute? = null) : AttributesFace()
}

sealed class QuestionFace(
    override val isClickable: Boolean
) : PlayCardContentState(
    isClickable = isClickable,
    rotation = CardRotation.FrontAlternate() // Assuming questions are on the alternate front rotation
) {
    data object ReadyToAnswer : QuestionFace(true)  // Waiting for user to start
    data object Answering : QuestionFace(false)
    data class AnswerScored(val answer: Answer, val isAnswerCorrect: Boolean) : QuestionFace(false)
}


sealed class CardRotation(
    open val rotationX: Float,
    open val rotationY: Float,
) {
    data class Back(
        override val rotationX: Float = -180f,
        override val rotationY: Float = 0f
    ) : CardRotation(rotationX, rotationY)

    data class FrontDefault(
        override val rotationX: Float = 0f,
        override val rotationY: Float = 0f
    ) : CardRotation(rotationX, rotationY)

    data class FrontAlternate(
        override val rotationX: Float = 0f,
        override val rotationY: Float = -180f
    ) : CardRotation(rotationX, rotationY)

    companion object {
        val ToVector: TwoWayConverter<CardRotation, AnimationVector2D> = TwoWayConverter(
            convertToVector = { state ->
                AnimationVector2D(state.rotationX, state.rotationY)
            },
            convertFromVector = { vector ->
                val rotationX = vector.v1
                val rotationY = vector.v2
                when {
                    rotationY in -180f..-90f && rotationX in -90f..0f -> FrontAlternate(rotationX, rotationY)
                    rotationY in -90f..0f && rotationX in -90f..0f -> FrontDefault(rotationX, rotationY)
                    else -> Back(rotationX, rotationY)
                }
            }
        )
    }
}