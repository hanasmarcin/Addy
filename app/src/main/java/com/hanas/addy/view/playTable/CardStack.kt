package com.hanas.addy.view.playTable

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hanas.addy.R
import com.hanas.addy.model.PlayingCard
import com.hanas.addy.ui.AppTheme
import com.hanas.addy.ui.PlayingCardFront
import com.hanas.addy.ui.drawPattern
import com.hanas.addy.ui.samplePlayingCardStack
import kotlin.random.Random

@Composable
fun BoxWithConstraintsScope.PlayingCardCover(
    data: PlayingCard,
    state: CardState,
    modifier: Modifier = Modifier,
    randomGenerator: Random = Random,
    maxRotateDegree: Int = 0
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
        .padding(start = 2.dp, bottom = 2.dp)
        .clip(RoundedCornerShape(24.dp))) {
        if (rotation > -90) PlayingCardFront(data)
        else CardBack()
    }
}

@Composable
private fun CardBack() {
    Box(
        Modifier
            .aspectRatio(0.6f)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .drawPattern(R.drawable.melt, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f))
    )
}

@Composable
private fun animateOffset(
    transition: Transition<CardState>, screenSizeInDp: DpSize, unscaledCardSizeInDp: DpSize
): State<Offset> {
    return transition.animateOffset(transitionSpec = { spec() }, label = "ccc") {
        val dpOffset = it.targetOffset(screenSizeInDp, unscaledCardSizeInDp)
        Offset(dpOffset.x.value, dpOffset.y.value)
    }
}

fun <T : Any> spec() = tween<T>(500)

@Composable
private fun animateWidth(
    transition: Transition<CardState>, screenSizeInDp: DpSize
) = transition.animateDp(transitionSpec = { spec() }, label = "bbb") {
    when (val cardWidth = it.targetWidth()) {
        ScreenWidth -> screenSizeInDp.width
        is Width -> cardWidth.value
    }
}

@Composable
private fun animateRotationZ(transition: Transition<CardState>) = transition.animateFloat(
    transitionSpec = { spec() }, label = "aaa"
) {
    it.targetRotationZ()
}

@Composable
private fun animateRotationX(transition: Transition<CardState>) = transition.animateFloat(
    transitionSpec = { spec() }, label = "aaa"
) {
    it.targetRotationX()
}

@Composable
private fun animateTransformOrigin(transition: Transition<CardState>) = transition.animateOffset(
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
fun PlayTable(data: PlayTableState, modifier: Modifier = Modifier) {
    val seed = rememberSaveable { Random.nextInt() }
    val randomGenerator by remember { derivedStateOf { Random(seed) } }
    val cards = data.toCardStateMap()
    BoxWithConstraints(modifier.fillMaxSize()) {
        cards.toSortedMap(compareBy { it.hashCode() }).forEach { (data, state) ->
            PlayingCardCover(
                modifier = Modifier
                    .align(Alignment.Center)
                    .layoutId(data.hashCode()),
                state = state,
                data = data,
                randomGenerator = randomGenerator,
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
                    PlayTableSegment(List(12) { cardStack[it] }),
                    PlayTableSegment(List(3) { cardStack[it + 12] }),
                    PlayTableSegment(List(5) { cardStack[it + 12 + 3] }),
                    PlayTableSegment(List(7) { cardStack[it + 12 + 3 + 5 + 7] }),
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
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

//@Composable
//fun NeatCardStack(
//    modifier: Modifier = Modifier, maxRotateDegree: Int = 2, cardCount: Int = 9
//) {
//    BoxWithConstraints(modifier.fillMaxSize()) {
//        val seed = rememberSaveable { Random.nextInt() }
//        val list = remember {
//            val x = List(cardCount) { CardState.OnUnusedStack(it, cardCount) }
//            mutableStateListOf<CardState>(*x.toTypedArray())
//        }
//        val randomGenerator by remember { derivedStateOf { Random(seed) } }
//        list.forEach {
//            PlayingCardCover(
//                state = it,
//                randomGenerator = randomGenerator,
//            )
//        }
//
//
//        LaunchedEffect(Unit) {
//            list.indices.reversed().onEach { index ->
//                delay(1000)
//                list[index] = CardState.InTopOpponentHand(cardCount - index - 1, cardCount)
//            }
//            delay(1000)
//            list.indices.onEachIndexed { index, cardState ->
//                delay(1000)
//                list[index] = CardState.OnCloseup(cardCount - index)
//                if (index - 1 >= 0) list[index - 1] = CardState.InHand(cardCount - index, cardCount)
//            }
//            delay(1000)
//            list[3] = CardState.OnCloseup(cardCount - 3)
//            list[cardCount - 1] = CardState.InHand(0, cardCount)
//            delay(1000)
//            list[3] = CardState.OnPlayStack(0)
//            (0..2).onEach {
//                list[it] = CardState.InHand(cardCount - it - 2, cardCount - 1)
//            }
//            (4..<cardCount).onEach {
//                list[it] = CardState.InHand(cardCount - it - 1, cardCount - 1)
//            }
//
//        }
//    }
//}
//
//@Preview
//@Composable
//fun NeatCardStackPreview() {
//    AppTheme {
//        Surface {
//            NeatCardStack(Modifier.padding(32.dp))
//        }
//    }
//}