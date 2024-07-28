package com.hanas.addy.view.playTable.view

import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hanas.addy.model.Answer
import com.hanas.addy.model.PlayCardData
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin
import com.hanas.addy.view.playTable.model.PlayCardContentUiState
import com.hanas.addy.view.playTable.model.PlayCardUiState

@Composable
fun CardOnTableLayout(
    screenSizeInDp: DpSize,
    data: PlayCardData,
    state: PlayCardUiState,
    modifier: Modifier = Modifier,
    onSelectAnswer: (Answer) -> Unit,
    onSelectToBattle: () -> Unit,
    startAnswer: () -> Unit,
    onClickCard: () -> Unit
) {
    val unscaledCardSizeInDp = DpSize(screenSizeInDp.width, screenSizeInDp.width / 0.6f)
    val transition = updateTransition(state, label = "Animate transition")
    val orientation by animateOrientation(transition)
    val rotationOnZ by animateRotationZ(transition)
    val width by animateWidth(transition, screenSizeInDp)
    val offset by animateOffset(transition, screenSizeInDp, unscaledCardSizeInDp)
    Box(modifier = modifier
        .zIndex(state.targetIndexZ())
        .offset {
            IntOffset(offset.x.dp.roundToPx(), offset.y.dp.roundToPx())
        }
        .graphicsLayer {
            val scale = width.toPx() / screenSizeInDp.width.toPx()
            scaleX = scale
            scaleY = scale
            rotationX = orientation.rotationX
            rotationY = orientation.rotationY
            rotationZ = rotationOnZ
            cameraDistance = (8 + 20 * (1 - scale)) * density

        }
        .clip(RoundedCornerShape(26.dp))
        .background(
            Color.Companion.Black
                .copy(alpha = 0.3f)
                .compositeOver(MaterialTheme.colorScheme.tertiary)
        )
        .padding(start = 2.dp, top = 2.dp)
        .clip(RoundedCornerShape(24.dp))

    ) {

        val contentState =
            transition.currentState.content.takeIf { it.type == orientation.contentType } ?: transition.targetState.content

        CardOnTableContent(
            data = data,
            contentState = contentState,
            isClickable = transition.isRunning.not() && state.clickOrigin != ClickOrigin.NOT_CLICKABLE,
            onClickCard = onClickCard,
            onSelectToBattle = onSelectToBattle,
            onSelectAnswer = onSelectAnswer,
            startAnswering = startAnswer,
        )
    }
}

@Composable
fun CardOnTableContent(
    data: PlayCardData,
    contentState: PlayCardContentUiState,
    isClickable: Boolean,
    onClickCard: () -> Unit,
    onSelectToBattle: () -> Unit,
    onSelectAnswer: (Answer) -> Unit,
    startAnswering: () -> Unit
) {
    val cardModifier = Modifier.clickable(enabled = isClickable) {
        onClickCard()
    }
    when (contentState) {
        is PlayCardContentUiState.AttributesDisplay -> {
            PlayCardAttributes(contentState, data, cardModifier, onSelectToBattle)
        }
        PlayCardContentUiState.BackCover -> {
            CardBack(cardModifier)
        }
        is PlayCardContentUiState.QuestionRace -> {
            PlayCardQuestion(data, contentState, cardModifier.graphicsLayer { scaleX = -1f }, startAnswering, onSelectAnswer)
        }
    }
}
