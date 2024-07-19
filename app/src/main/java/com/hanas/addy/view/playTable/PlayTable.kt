package com.hanas.addy.view.playTable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.hanas.addy.model.PlayingCardData
import com.hanas.addy.ui.AppTheme
import com.hanas.addy.ui.PlayingCardBack
import com.hanas.addy.ui.PlayingCardFront
import com.hanas.addy.ui.samplePlayingCardStack
import kotlin.random.Random



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
    onClickUpdateState: (PlayingCardState) -> Unit
) {
    val seed = rememberSaveable { Random.nextInt() }
    val cards = data.toCardStateMap()
    BoxWithConstraints(modifier.fillMaxSize()) {
        cards.toSortedMap(compareBy { it.hashCode() }).forEach { (data, state) ->
            CardOnTable(
                modifier = Modifier
                    .align(Alignment.Center)
                    .layoutId(data.hashCode()),
                state = state,
                data = data,
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
                    .padding(horizontal = 16.dp),
            ) {}
        }
    }
}