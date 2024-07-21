package com.hanas.addy.view.playTable

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hanas.addy.R
import com.hanas.addy.model.Answer
import com.hanas.addy.model.PlayingCardData
import com.hanas.addy.repository.samplePlayingCardStack
import com.hanas.addy.ui.theme.AppTheme

@Composable
fun BoxWithConstraintsScope.CardOnTable(
    data: PlayingCardData,
    state: PlayingCardState,
    modifier: Modifier = Modifier,
    onSelectAnswer: (Answer) -> Unit,
    onClickUpdateState: (PlayingCardState) -> Unit
) {
    val screenSizeInDp = with(LocalDensity.current) {
        DpSize(constraints.maxWidth.toDp(), constraints.maxHeight.toDp())
    }
    val unscaledCardSizeInDp = DpSize(screenSizeInDp.width, screenSizeInDp.width / 0.6f)
    val transition = updateTransition(state, label = "Animate transition")
    val rotation by animateRotationX(transition)
    val zRotation by animateRotationZ(transition)
    val width by animateWidth(transition, screenSizeInDp)
    val offset by animateOffset(transition, screenSizeInDp, unscaledCardSizeInDp)
    val animTransformOrigin by animateTransformOrigin(transition)
    Box(modifier = modifier
        .align(Alignment.Center)
        .zIndex(
            state
                .targetIndexZ()
                .toFloat()
        )
        .offset {
            IntOffset(offset.x.dp.roundToPx(), offset.y.dp.roundToPx())
        }
        .graphicsLayer {
//                        rotationZ = (randomGenerator.nextFloat() * 2 * maxRotateDegree - maxRotateDegree)
            rotationZ = zRotation
            val scale = width.toPx() / screenSizeInDp.width.toPx()
            transformOrigin = TransformOrigin(animTransformOrigin.x, animTransformOrigin.y)
            scaleX = scale
            scaleY = scale
            rotationX = rotation
            cameraDistance = (8 + 20 * (1 - scale)) * density

        }
        .clip(RoundedCornerShape(26.dp))
        .background(
            Color.Companion.Black
                .copy(alpha = 0.3f)
                .compositeOver(MaterialTheme.colorScheme.tertiary)
        )
        .padding(start = 2.dp, top = 2.dp)
        .clip(RoundedCornerShape(24.dp))) {
        val cardModifier = Modifier.clickable {
            transition.currentState.takeIf { it == transition.targetState }?.let(onClickUpdateState)
        }
//        var frontState by remember { mutableStateOf(true) }
        if (rotation > -90) //AnimatedContent(frontState, label = "") {
//            if (it) {
            PlayingCardQuestion(data, cardModifier, onSelectAnswer)
//                PlayingCardFront(data, cardModifier)
//            } else {
//                PlayingCardBack(data, cardModifier)
//            }
//        }
        else CardBack(cardModifier)
    }
}

@Composable
private fun CardBack(modifier: Modifier = Modifier) {
    Box(
        modifier
            .rotate(180f)
            .aspectRatio(0.6f)
            .fillMaxSize()
            .paperBackground(rememberPaperBrush(), MaterialTheme.colorScheme.primary)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Image(
            painterResource(R.drawable.book_pattern_bauhaus_imagen),
            null,
//            .border(16.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Inside,
            modifier = Modifier.fillMaxSize()

//            .background(MaterialTheme.colorScheme.tertiary)
//            .padding(16.dp)
//            .clip(RoundedCornerShape(8.dp))
        )
    }
}

@Composable
private fun animateOffset(
    transition: Transition<PlayingCardState>, screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize
): State<Offset> {
    return transition.animateOffset(transitionSpec = { spec() }, label = "ccc") {
        val dpOffset = it.targetOffset(screenSizeInDp, unscaledCardSizeInDp)
        Offset(dpOffset.x.value, dpOffset.y.value)
    }
}

fun <T : Any> spec() = tween<T>(500)

@Composable
private fun animateWidth(
    transition: Transition<PlayingCardState>, screenSizeInDp: DpSize
) = transition.animateDp(transitionSpec = { spec() }, label = "bbb") {
    when (val cardWidth = it.targetWidth()) {
        ScreenWidth -> screenSizeInDp.width
        is Width -> cardWidth.value
    }
}

@Composable
private fun animateRotationZ(transition: Transition<PlayingCardState>) = transition.animateFloat(
    transitionSpec = { spec() }, label = "aaa"
) {
    it.targetRotationZ()
}

@Composable
private fun animateRotationX(transition: Transition<PlayingCardState>) = transition.animateFloat(
    transitionSpec = { spec() }, label = "aaa"
) {
    it.targetRotationX()
}

@Composable
private fun animateTransformOrigin(transition: Transition<PlayingCardState>) = transition.animateOffset(
    transitionSpec = { spec() }, label = "aaa"
) {
    it.targetTransformOrigin()
}

@Composable
private fun rememberScreenSizeInPx(): Size {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    return remember(density, configuration) {
        with(density) {
            Size(
                configuration.screenWidthDp.dp.toPx(), configuration.screenHeightDp.dp.toPx()
            )
        }
    }
}

@Composable
private fun rememberScreenSizeInDp(): DpSize {
    val configuration = LocalConfiguration.current
    return remember(configuration) {
        DpSize(
            configuration.screenWidthDp.dp, configuration.screenHeightDp.dp
        )
    }
}

@Composable
fun PlayTable(
    data: PlayTableState,
    modifier: Modifier = Modifier,
    onClickAwayFromCloseUp: () -> Unit,
    onSelectAnswer: (PlayingCardState, Answer) -> Unit,
    onClickUpdateState: (PlayingCardState) -> Unit
) {
    val cards = data.toCardStateMap()
    BoxWithConstraints(modifier.fillMaxSize()) {
        if (data.closeUp != null) {
            Box(
                Modifier
                    .background(DrawerDefaults.scrimColor)
                    .zIndex(700f)
                    .fillMaxSize()
                    .clickable { onClickAwayFromCloseUp() }
            )
        }
        cards.toSortedMap(compareBy { it.hashCode() }).forEach { (data, state) ->
            CardOnTable(
                data = data,
                state = state,
                modifier = Modifier
                    .align(Alignment.Center)
                    .layoutId(data.hashCode()),
                onSelectAnswer = { onSelectAnswer(state, it) },
                onClickUpdateState = onClickUpdateState
            )
        }
    }
}

@Preview
@Composable
fun PlayTablePreview() {
    AppTheme {
        val cardStack = samplePlayingCardStack.cards
        var playTableState by remember {
            mutableStateOf(
                PlayTableState(
                    PlayTableState.Segment(List(12) { cardStack[it] }),
                    PlayTableState.Segment(List(3) { cardStack[it + 12] }),
                    PlayTableState.Segment(List(5) { cardStack[it + 12 + 3] }),
                )
            )
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
                {}, { _, _ -> }) {}
        }
    }
}