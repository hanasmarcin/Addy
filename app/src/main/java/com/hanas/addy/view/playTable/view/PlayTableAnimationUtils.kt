package com.hanas.addy.view.playTable.view

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import com.hanas.addy.view.playTable.model.PlayCardContentUiState
import com.hanas.addy.view.playTable.model.PlayCardContentUiType.ATTRIBUTES
import com.hanas.addy.view.playTable.model.PlayCardContentUiType.BACK_COVER
import com.hanas.addy.view.playTable.model.PlayCardContentUiType.QUESTION
import com.hanas.addy.view.playTable.model.PlayCardOrientation
import com.hanas.addy.view.playTable.model.PlayCardUiState


@Composable
fun animateOffset(
    transition: Transition<PlayCardUiState>, screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize
): State<Offset> {
    return transition.animateOffset(transitionSpec = { spec() }, label = "ccc") {
        val dpOffset = it.targetOffset(screenSizeInDp, unscaledCardSizeInDp)
        Offset(dpOffset.x.value, dpOffset.y.value)
    }
}

fun <T : Any> spec() = tween<T>(500)

@Composable
fun animateWidth(
    transition: Transition<PlayCardUiState>, screenSizeInDp: DpSize
) = transition.animateDp(transitionSpec = { spec() }, label = "bbb") {
    val width = it.targetWidth()
    if (width == Dp.Infinity) screenSizeInDp.width
    else width

}

@Composable
fun animateRotationZ(transition: Transition<PlayCardUiState>) = transition.animateFloat(
    transitionSpec = { spec() }, label = "aaa"
) {
    it.targetRotationZ()
}

@Composable
fun animateOrientation(transition: Transition<PlayCardUiState>) = transition.animateValue(
    typeConverter = PlayCardOrientation.ToVector,
    transitionSpec = { spec() }, label = "aaa"
) {
    PlayCardOrientation(
        it.content.rotationX, it.content.rotationY, when (it.content) {
            is PlayCardContentUiState.AttributesDisplay -> ATTRIBUTES
            PlayCardContentUiState.BackCover -> BACK_COVER
            is PlayCardContentUiState.QuestionRace -> QUESTION
        }
    )
}