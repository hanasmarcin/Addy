package com.hanas.addy.view.playTable.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hanas.addy.R
import com.hanas.addy.model.Answer
import com.hanas.addy.repository.gemini.samplePlayCardStack
import com.hanas.addy.ui.drawPattern
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin
import com.hanas.addy.view.playTable.PlayerState
import com.hanas.addy.view.playTable.PositionOnTable
import com.hanas.addy.view.playTable.model.AttributesFace
import com.hanas.addy.view.playTable.model.CardCollection
import com.hanas.addy.view.playTable.model.CardSlot
import com.hanas.addy.view.playTable.model.PlayTableState
import com.hanas.addy.view.playTable.model.QuestionFace
import com.hanas.addy.view.playTable.view.cardcontent.CardOnTableLayout

@Composable
fun PlayTable(
    state: PlayTableState,
    modifier: Modifier = Modifier,
    onClickAwayFromCloseUp: () -> Unit,
    onSelectAnswer: (Long, Answer) -> Unit,
    onSelectToBattle: (Long) -> Unit,
    onClickCard: (Long, ClickOrigin) -> Unit,
    onStartAnswer: (Long) -> Unit,
    onSelectAttribute: (Int) -> Unit,
) {
    val cards = state.toCardStateMap()
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
            enabled = state.closeUp != null,
            isClickAvailable = (state.closeUp?.contentState is AttributesFace),
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
                        .layoutId(state.hashCode()),
                    onSelectAnswer = { onSelectAnswer(cardId, it) },
                    onSelectToBattle = { onSelectToBattle(cardId) },
                    onClickCard = { onClickCard(cardId, ClickOrigin.PLAYER_HAND) },
                    startAnswer = { onStartAnswer(cardId) },
                    onSelectAttribute = onSelectAttribute
                )
            }
        }
        state.players.forEach { (position, player) ->
            key(position) {
                PlayerLabel(position, player)
            }
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.PlayerLabel(
    position: PositionOnTable,
    player: PlayerState,
) {
    Row(Modifier
        .zIndex(700f)
        .align(Alignment.CenterEnd)
        .offset {
            when (position) {
                PositionOnTable.BOTTOM -> IntOffset(0, (maxHeight / 2 - 120.dp).roundToPx())
                PositionOnTable.TOP -> IntOffset(0, -(maxHeight / 2 - 120.dp).roundToPx())
                PositionOnTable.START -> TODO()
                PositionOnTable.END -> TODO()
            }
        }
        .clip(RoundedCornerShape(8.dp))
        .background(position.color)
        .padding(4.dp, 2.dp, 4.dp, 8.dp)
        .clip(RoundedCornerShape(6.dp)),
        horizontalArrangement = spacedBy(2.dp)
    ) {
        Text(
            player.name,
            Modifier
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 4.dp, horizontal = 8.dp)
        )
        Text(
            player.points.toString(),
            Modifier
                .clip(RoundedCornerShape(2.dp))
                .background(
                    position.color
                        .copy(alpha = 0.7f)
                        .compositeOver(MaterialTheme.colorScheme.surface)
                )
                .padding(vertical = 4.dp, horizontal = 8.dp)
        )
    }
}

@Composable
fun Scrim(
    enabled: Boolean,
    isClickAvailable: Boolean,
    scrimSize: IntSize? = null,
    onClickAwayFromCloseUp: () -> Unit
) {
    AnimatedVisibility(enabled, Modifier
        .fillMaxSize()
        .zIndex(700f)
        .layout { measurable, constraints ->
            val placeable = measurable.measure(
                scrimSize?.let { Constraints.fixed(it.width, it.height) } ?: constraints
            )
            layout(placeable.width, placeable.height) {
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

@Preview(showSystemUi = true)
@Composable
fun PlayTablePreview() {
    AppTheme {
        val cardStack = samplePlayCardStack.cards
        var playTableState by remember {
            mutableStateOf(
                PlayTableState(
                    mapOf(
                        PositionOnTable.BOTTOM to PlayerState("a", "Marcin", 1),
                        PositionOnTable.TOP to PlayerState("n", "Edward", 2)
                    ),
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
                    .drawPattern(R.drawable.graph_paper, tint = Color.Black.copy(alpha = 0.05f))
                    .fillMaxSize()
                    .padding(32.dp),
                {}, { _, _ -> }, {}, { _, _ -> }, {}, { })
        }
    }
}