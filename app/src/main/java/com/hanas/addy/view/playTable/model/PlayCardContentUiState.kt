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
    val type: PlayCardContentUiType
) {
    data object BackCover : PlayCardContentUiState(-180f, 0f, BACK_COVER)
    sealed class AttributesDisplay : PlayCardContentUiState(0f, 0f, ATTRIBUTES) {
        data object Initial : AttributesDisplay()
        data class AddingBoost(val boostForRed: Int, val boostForGreen: Int, val boostForBlue: Int) : AttributesDisplay()
        data object WaitingForAttributeBattle : AttributesDisplay()

    }

    sealed class QuestionRace(val targetImageHeight: Dp) : PlayCardContentUiState(0f, -180f, QUESTION) {
        data object Initial : QuestionRace(Dp.Infinity)
        data object Answering : QuestionRace(0.dp)
        data class Result(val answer: Answer, val isAnswerCorrect: Boolean) : QuestionRace(0.dp)
    }
}
