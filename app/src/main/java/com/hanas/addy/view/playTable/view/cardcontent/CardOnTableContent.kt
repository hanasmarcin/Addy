package com.hanas.addy.view.playTable.view.cardcontent

import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hanas.addy.R
import com.hanas.addy.model.Answer
import com.hanas.addy.model.PlayCardData
import com.hanas.addy.ui.samplePlayCard
import com.hanas.addy.ui.theme.AppColors.green
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.playTable.model.AttributesFace
import com.hanas.addy.view.playTable.model.BackFace
import com.hanas.addy.view.playTable.model.PlayCardContentState
import com.hanas.addy.view.playTable.model.QuestionFace
import com.hanas.addy.view.playTable.view.animateOffset
import com.hanas.addy.view.playTable.view.animateOrientation
import com.hanas.addy.view.playTable.view.animateRotationZ
import com.hanas.addy.view.playTable.view.animateWidth
import com.hanas.addy.view.playTable.view.uistate.PlayCardUiState

const val CARD_ASPECT_RATIO = 0.55f

@Composable
fun CardOnTableLayout(
    screenSizeInDp: DpSize,
    state: PlayCardUiState,
    modifier: Modifier = Modifier,
    onSelectAnswer: (Answer) -> Unit,
    onSelectToBattle: () -> Unit,
    onSelectAttribute: (Int) -> Unit,
    startAnswer: () -> Unit,
    onClickCard: () -> Unit
) {
    val unscaledCardSizeInDp = DpSize(screenSizeInDp.width, screenSizeInDp.width / CARD_ASPECT_RATIO)
    val placementTransition = updateTransition(state.placement, label = "Animate placement")
    val contentTransition = updateTransition(state.contentState, label = "Animate content state")
    val orientation by animateOrientation(contentTransition)
    val rotationOnZ by animateRotationZ(placementTransition)
    val width by animateWidth(placementTransition, screenSizeInDp)
    val offset by animateOffset(placementTransition, screenSizeInDp, unscaledCardSizeInDp)
    Surface(
        modifier = modifier
            .zIndex(state.placement.targetZIndex())
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
            .clip(RoundedCornerShape(14.dp))
            .background(green)
            .padding(start = 2.dp, bottom = 2.dp)
            .clip(RoundedCornerShape(12.dp)),
    ) {
        val contentState by remember(contentTransition.targetState, contentTransition.currentState, orientation) {
            derivedStateOf {
                when {
                    contentTransition.targetState.rotation::class == orientation::class -> contentTransition.targetState
                    contentTransition.currentState.rotation::class == orientation::class -> contentTransition.currentState
                    else -> contentTransition.targetState
                }
            }
        }
        CardOnTableContent(
            data = state.data,
            contentState = contentState,
            isClickable = placementTransition.isRunning.not() && with(contentTransition) { currentState.isClickable && targetState.isClickable },
            onClickCard = onClickCard,
            onSelectToBattle = onSelectToBattle,
            onSelectAnswer = onSelectAnswer,
            startAnswering = startAnswer,
            onSelectAttribute = onSelectAttribute
        )
    }
}

@Composable
fun CardOnTableContent(
    data: PlayCardData,
    contentState: PlayCardContentState,
    isClickable: Boolean,
    onClickCard: () -> Unit,
    onSelectToBattle: () -> Unit,
    onSelectAnswer: (Answer) -> Unit,
    startAnswering: () -> Unit,
    onSelectAttribute: (Int) -> Unit
) {
    val cardModifier = Modifier.clickable(enabled = isClickable) {
        onClickCard()
    }
    when (contentState) {
        is AttributesFace -> {
            PlayCardAttributes(contentState, data, cardModifier, onSelectToBattle, onSelectAttribute)
        }
        is BackFace -> {
            when (contentState) {
                BackFace.BackCover -> CardBack(cardModifier)
                BackFace.OpponentAnswering -> PlayCardOpponentOnBattleSlot(cardModifier, null)
                BackFace.OpponentWaitingForAttributeBattle -> PlayCardOpponentOnBattleSlot(cardModifier, true)
            }
        }
        is QuestionFace -> {
            PlayCardQuestion(data, contentState, cardModifier.graphicsLayer { scaleX = -1f }, startAnswering, onSelectAnswer)
        }
    }
}

@Composable
fun PlayCardOpponentOnBattleSlot(modifier: Modifier, answeredCorrectly: Boolean?) {
    Box(
        modifier
            .rotate(180f)
            .aspectRatio(CARD_ASPECT_RATIO)
            .fillMaxSize()
            .paperBackground(rememberPaperBrush(), MaterialTheme.colorScheme.primary)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painterResource(R.drawable.book_pattern_bauhaus_imagen),
            null,
            contentScale = ContentScale.Inside,
            modifier = Modifier.fillMaxSize()
        )
        when (answeredCorrectly) {
            true -> Icon(
                Icons.Default.Done, null, tint = Color.Green, modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f, false)
            )
            false -> Icon(
                Icons.Default.Clear, null, tint = Color.Red, modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f, false)
            )
            null -> Icon(
                Icons.Default.Lock, null, tint = Color.White, modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f, false)
            )
        }
    }

}

class PlayCardProvider : PreviewParameterProvider<PlayCardContentState> {
    override val values = sequenceOf(
        AttributesFace.StaticPreview,
        QuestionFace.ReadyToAnswer,
        QuestionFace.Answering,
        QuestionFace.AnswerScored(Answer.B, true),
        QuestionFace.AnswerScored(Answer.C, false),
        AttributesFace.AddingBoost(3, 0, -1),
        AttributesFace.WaitingForActiveAttributeSelected,
        BackFace.BackCover,
        BackFace.OpponentAnswering,
        BackFace.OpponentWaitingForAttributeBattle
    )
}

@Preview
@Composable
fun PlayCardAnimationPreview() {
    AppTheme {
        var state by remember { mutableStateOf<QuestionFace>(QuestionFace.ReadyToAnswer) }
        Card(shape = RoundedCornerShape(24.dp)) {
            CardOnTableContent(
                samplePlayCard, state, false, {}, {},
                {
                    state = QuestionFace.AnswerScored(it, it == Answer.A)
                },
                {
                    state = QuestionFace.Answering
                },
                {}
            )
        }
    }
}

@Preview
@Composable
fun PlayCardPreview(@PreviewParameter(PlayCardProvider::class) state: PlayCardContentState) {
    AppTheme {
        Card(shape = RoundedCornerShape(24.dp)) {
            CardOnTableContent(samplePlayCard, state, false, {}, {}, {}, {}) {}
        }
    }
}