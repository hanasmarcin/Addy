package com.hanas.addy.view.playTable

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hanas.addy.model.Answer
import com.hanas.addy.model.PlayCardData
import com.hanas.addy.repository.gemini.samplePlayCardStack
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.playTable.PlayCardContentUiType.ATTRIBUTES
import com.hanas.addy.view.playTable.PlayCardContentUiType.BACK_COVER
import com.hanas.addy.view.playTable.PlayCardContentUiType.QUESTION
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin
import kotlinx.coroutines.delay

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
    val animTransformOrigin by animateTransformOrigin(transition)
    Box(modifier = modifier
        .zIndex(
            state
                .targetIndexZ()
                .toFloat()
        )
        .offset {
            IntOffset(offset.x.dp.roundToPx(), offset.y.dp.roundToPx())
        }
        .graphicsLayer {
            val scale = width.toPx() / screenSizeInDp.width.toPx()
            transformOrigin = TransformOrigin(animTransformOrigin.x, animTransformOrigin.y)
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
            isTransitioning = transition.isRunning,
            clickOrigin = state.clickOrigin,
            onClickCard = onClickCard,
            onSelectToBattle = onSelectToBattle,
            onSelectAnswer = onSelectAnswer,
            startAnswering = startAnswer,
        )
    }
}

@Composable
private fun animateOffset(
    transition: Transition<PlayCardUiState>, screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize
): State<Offset> {
    return transition.animateOffset(transitionSpec = { spec() }, label = "ccc") {
        val dpOffset = it.targetOffset(screenSizeInDp, unscaledCardSizeInDp)
        Offset(dpOffset.x.value, dpOffset.y.value)
    }
}

fun <T : Any> spec() = tween<T>(500)

@Composable
private fun animateWidth(
    transition: Transition<PlayCardUiState>, screenSizeInDp: DpSize
) = transition.animateDp(transitionSpec = { spec() }, label = "bbb") {
    val width = it.targetWidth()
    if (width == Dp.Infinity) screenSizeInDp.width
    else width

}

@Composable
private fun animateRotationZ(transition: Transition<PlayCardUiState>) = transition.animateFloat(
    transitionSpec = { spec() }, label = "aaa"
) {
    it.targetRotationZ()
}

@Composable
private fun animateOrientation(transition: Transition<PlayCardUiState>) = transition.animateValue(
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

@Composable
private fun animateTransformOrigin(transition: Transition<PlayCardUiState>) = transition.animateOffset(
    transitionSpec = { spec() }, label = "aaa"
) {
    it.targetTransformOrigin()
}

@Composable
fun PlayTable(
    data: PlayTableState,
    modifier: Modifier = Modifier,
    onClickAwayFromCloseUp: () -> Unit,
    onSelectAnswer: (Long, Answer) -> Unit,
    onSelectToBattle: (Long) -> Unit,
    onClickCard: (Long, ClickOrigin) -> Unit,
    onStartAnswer: (Long) -> Unit
) {
    val cards = data.toCardStateMap()
    BoxWithConstraints(modifier.fillMaxSize()) {
        val screenSizeInDp = with(LocalDensity.current) {
            DpSize(constraints.maxWidth.toDp(), constraints.maxHeight.toDp())
        }
        if (data.closeUp != null) {
            Box(
                Modifier
                    .background(DrawerDefaults.scrimColor)
                    .zIndex(700f)
                    .fillMaxSize()
                    .clickable { onClickAwayFromCloseUp() }
            )
        }
        cards.forEach { (data, state) ->
            key(data.id) {
                CardOnTableLayout(
                    data = data,
                    screenSizeInDp = screenSizeInDp,
                    state = state,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .layoutId(data.hashCode()),
                    onSelectAnswer = { onSelectAnswer(data.id, it) },
                    onSelectToBattle = { onSelectToBattle(data.id) },
                    onClickCard = { state.clickOrigin?.let { onClickCard(data.id, it) } },
                    startAnswer = { onStartAnswer(data.id) }
                )
            }
        }
    }
}

@Preview
@Composable
fun PlayTablePreview() {
    AppTheme {
        val cardStack = samplePlayCardStack.cards
        var playTableState by remember {
            mutableStateOf(
                PlayTableState(
                    PlayTableState.Segment(emptyList()),
                    PlayTableState.Segment(emptyList()),
                    PlayTableState.Segment(emptyList()),
                )
            )
        }
        LaunchedEffect(Unit) {
            for (i in 0..20) {
                delay(600)
                playTableState = PlayTableState(
                    PlayTableState.Segment(cardStack.take(i)),
                    PlayTableState.Segment(emptyList()),
                    PlayTableState.Segment(emptyList()),
                )
            }
            for (i in 0..10) {
                delay(600)
                val x = playTableState.unusedStack.cards.last()
                playTableState = PlayTableState(
                    PlayTableState.Segment(playTableState.unusedStack.cards.drop(1)),
                    PlayTableState.Segment(playTableState.playerHand.cards + x),
                    PlayTableState.Segment(emptyList()),
                )
            }
        }
//        LaunchedEffect(Unit) {
//            delay(500)
//            playTableState = PlayTableState(
//                List(10) { cardStack[it] },
//                List(3) { cardStack[1 + it + 11] },
//                List(5) { cardStack[it + 12 + 3] } + cardStack[11] + cardStack[12],
//                List(7) { cardStack[it + 12 + 3 + 5 + 7] } + cardStack[10],
//            )
//        }
        Surface {
            PlayTable(
                playTableState,
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                {}, { _, _ -> }, {}, { _, _ -> }, {})
        }
    }
}