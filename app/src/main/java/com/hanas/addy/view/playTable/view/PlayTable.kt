package com.hanas.addy.view.playTable.view

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import com.hanas.addy.view.playTable.model.AttributesFace
import com.hanas.addy.view.playTable.model.CardCollection
import com.hanas.addy.view.playTable.model.CardSlot
import com.hanas.addy.view.playTable.model.PlayTableState
import com.hanas.addy.view.playTable.model.QuestionFace
import com.hanas.addy.view.playTable.view.cardcontent.CardOnTableLayout

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
        Scrim(
            enabled = data.closeUp != null,
            isClickAvailable = (data.closeUp?.contentState is AttributesFace),
            scrimSize = scrimSize,
            onClickAwayFromCloseUp = onClickAwayFromCloseUp
        )
        cards.forEach { (cardId, state) ->
            key(cardId) {
                CardOnTableLayout(
                    state = state,
                    screenSizeInDp = screenSizeInDp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .layoutId(data.hashCode()),
                    onSelectAnswer = { onSelectAnswer(cardId, it) },
                    onSelectToBattle = { onSelectToBattle(cardId) },
                    onClickCard = { onClickCard(cardId, ClickOrigin.PLAYER_HAND) },
                    startAnswer = { onStartAnswer(cardId) }
                )
            }
        }
    }
}

@Composable
private fun Scrim(
    enabled: Boolean,
    isClickAvailable: Boolean,
    scrimSize: IntSize,
    onClickAwayFromCloseUp: () -> Unit
) {
    AnimatedVisibility(enabled, Modifier
        .fillMaxSize()
        .zIndex(700f)
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
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(DrawerDefaults.scrimColor)
                .clickable(isClickAvailable) { onClickAwayFromCloseUp() }
        )
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
                    CardCollection(listOf(cardStack[0])),
                    CardCollection(listOf(cardStack[1], cardStack[5], cardStack[6])),
                    CardCollection(listOf(cardStack[2], cardStack[7], cardStack[8])),
                    playerBattleSlot = CardSlot(
                        cardStack[3], contentState = QuestionFace.Answering
                    ),
                    opponentBattleSlot = CardSlot(
                        cardStack[4], contentState = QuestionFace.Answering
                    )
                )
            )
        }
//        LaunchedEffect(Unit) {
//            for (i in 0..20) {
//                playTableState = PlayTableState(
//                    PlayTableState.Segment(cardStack.take(i)),
//                    PlayTableState.Segment(emptyList()),
//                    PlayTableState.Segment(emptyList()),
//                )
//            }
//            delay(600)
//            playTableState = PlayTableState(
//                PlayTableState.Segment(playTableState.unusedStack.cards.drop(5)),
//                PlayTableState.Segment(listOf(playTableState.unusedStack.cards[0])),
//                PlayTableState.Segment(listOf(playTableState.unusedStack.cards[1])),
//                opponentBattleSlot = PlayTableState.CardSlot(
//                    playTableState.unusedStack.cards[2], PlayTableSegmentType.PLAYER_HAND, 0
//                )
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