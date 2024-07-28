package com.hanas.addy.view.playTable.view

import android.util.Log
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateOffset
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hanas.addy.model.Answer
import com.hanas.addy.repository.gemini.samplePlayCardStack
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin
import com.hanas.addy.view.playTable.model.PlayCardUiState
import com.hanas.addy.view.playTable.model.PlayTableSegmentType
import com.hanas.addy.view.playTable.model.PlayTableState
import kotlinx.coroutines.delay

@Composable
fun animateTransformOrigin(transition: Transition<PlayCardUiState>) = transition.animateOffset(
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
    var scrimSize by remember { mutableStateOf(IntSize.Zero) }
    BoxWithConstraints(modifier
        .onPlaced {
            scrimSize = it.parentCoordinates?.size ?: it.size
        }
        .fillMaxSize()) {
        val screenSizeInDp = with(LocalDensity.current) {
            DpSize(constraints.maxWidth.toDp(), constraints.maxHeight.toDp())
        }
        if (data.closeUp != null) {
            Box(
                Modifier
                    .onPlaced {
                        Log.d(
                            "HANASSS",
                            "placed:${it.size}, ${it.positionInRoot()}, ${it.isAttached}, ${it.providedAlignmentLines}, ${it.parentCoordinates?.parentCoordinates?.size}, ${it.parentLayoutCoordinates?.parentCoordinates?.size}"
                        )
                    }
                    .fillMaxSize()
                    .layout { measurable, constraints ->
                        val size = scrimSize
                        Log.d("HANASSS", "constraints: $constraints")
                        val placeable = measurable.measure(
                            Constraints.fixed(size.width, size.height)
                        )
                        layout(size.width, size.height) {
                            placeable.place(0, 0)
                        }
                    }
                    .background(DrawerDefaults.scrimColor)
                    .zIndex(700f)
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
                playTableState = PlayTableState(
                    PlayTableState.Segment(cardStack.take(i)),
                    PlayTableState.Segment(emptyList()),
                    PlayTableState.Segment(emptyList()),
                )
            }
            delay(600)
            playTableState = PlayTableState(
                PlayTableState.Segment(playTableState.unusedStack.cards.drop(5)),
                PlayTableState.Segment(listOf(playTableState.unusedStack.cards[0])),
                PlayTableState.Segment(listOf(playTableState.unusedStack.cards[1])),
                closeUp = PlayTableState.CardSlot(
                    playTableState.unusedStack.cards[2], PlayTableSegmentType.PLAYER_HAND, 0
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
                    .padding(32.dp),
                {}, { _, _ -> }, {}, { _, _ -> }, {})
        }
    }
}