package com.hanas.addy.view.playTable.view

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
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hanas.addy.model.Answer
import com.hanas.addy.repository.gemini.samplePlayCardStack
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin
import com.hanas.addy.view.playTable.model.PlayCardUiState
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