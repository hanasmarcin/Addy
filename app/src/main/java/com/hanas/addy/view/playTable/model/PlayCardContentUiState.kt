package com.hanas.addy.view.playTable.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hanas.addy.model.Answer
import com.hanas.addy.view.playTable.model.PlayCardContentUiType.ATTRIBUTES
import com.hanas.addy.view.playTable.model.PlayCardContentUiType.BACK_COVER
import com.hanas.addy.view.playTable.model.PlayCardContentUiType.QUESTION

enum class PlayCardContentUiType {
    BACK_COVER, ATTRIBUTES, QUESTION
}

sealed class PlayCardContentUiState(
    val rotationX: Float,
    val rotationY: Float,
    open val isClickable: Boolean,
    val type: PlayCardContentUiType
) {

    data object BackCover : PlayCardContentUiState(
        rotationX = -180f,
        rotationY = 0f,
        isClickable = false,
        type = BACK_COVER
    )

    sealed class AttributesDisplay : PlayCardContentUiState(
        rotationX = 0f,
        rotationY = 0f,
        isClickable = true,
        type = ATTRIBUTES
    ) {
        data object Initial : AttributesDisplay()
        data class AddingBoost(val boostForRed: Int, val boostForGreen: Int, val boostForBlue: Int) : AttributesDisplay()
        data object WaitingForAttributeBattle : AttributesDisplay()

    }

    data object ChooseActiveAttribute : PlayCardContentUiState(
        rotationX = 0f,
        rotationY = 0f,
        isClickable = false,
        type = ATTRIBUTES
    )

    data object OpponentAnswering : PlayCardContentUiState(
        rotationX = -180f,
        rotationY = 0f,
        isClickable = false,
        type = BACK_COVER
    )

    data object OpponentWaitingForAttributeBattle : PlayCardContentUiState(
        rotationX = -180f,
        rotationY = 0f,
        isClickable = false,
        type = BACK_COVER
    )



    sealed class QuestionRace(val targetImageHeight: Dp, override val isClickable: Boolean) : PlayCardContentUiState(
        rotationX = 0f,
        rotationY = -180f,
        isClickable = isClickable,
        type = QUESTION
    ) {
        data object Initial : QuestionRace(Dp.Infinity, true)
        data object Answering : QuestionRace(0.dp, false)
        data class Result(val answer: Answer, val isAnswerCorrect: Boolean) : QuestionRace(0.dp, false)
    }
}
