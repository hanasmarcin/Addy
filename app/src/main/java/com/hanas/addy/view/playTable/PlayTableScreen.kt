package com.hanas.addy.view.playTable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.ui.AppTheme
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.samplePlayingCardStack
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
object PlayTable : NavScreen

fun NavGraphBuilder.playTableComposable() {
    composable<PlayTable> {
        val viewModel = koinNavViewModel<PlayTableViewModel>()
        val state by viewModel.playTableStateFlow.collectAsState()
        val lockInput by viewModel.lockInputFlow.collectAsState()
        PlayTableScreen(state, lockInput, viewModel::onClickBox)
    }
}

@Composable
fun PlayTableScreen(state: PlayTableState, lockInput: Boolean, onClickBox: (ClickBoxPosition) -> Unit) {
    Surface {
        PlayTable(
            state,
            Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        )
//        if (state.closeUp == null) {
        ClickBox(
            onClick = onClickBox,
            enabled = lockInput.not(),
            cardsInHandAmount = state.playerHand.availableSlots
        )
//        }
    }
}

sealed class ClickBoxPosition {
    data object Top : ClickBoxPosition()
    data object Start : ClickBoxPosition()
    data object End : ClickBoxPosition()
    data class Bottom(val index: Int) : ClickBoxPosition()
}

@Composable
private fun ClickBox(
    cardsInHandAmount: Int,
    enabled: Boolean,
    onClick: (ClickBoxPosition) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Red.copy(alpha = 0.2f))
                .weight(1f)
                .clickable(enabled = enabled) { onClick(ClickBoxPosition.Top) }
        )
        Row(
            Modifier
                .fillMaxSize()
                .weight(2f)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Yellow.copy(alpha = 0.2f))
                    .weight(1f)
                    .clickable(enabled = enabled) { onClick(ClickBoxPosition.Start) }
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Magenta.copy(alpha = 0.2f))
                    .weight(1f)
                    .clickable(enabled = enabled) { onClick(ClickBoxPosition.End) }
            )
        }
        Row(
            Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(32.dp)
                    .background(Color.Red.copy(alpha = 0.5f / cardsInHandAmount))
                    .clickable(enabled = enabled) { onClick(ClickBoxPosition.Bottom(0)) }
            )
            (0 until cardsInHandAmount).forEach {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Red.copy(alpha = (it + 1f) / 2f / cardsInHandAmount))
                        .weight(if (it == cardsInHandAmount - 1) 2f else 1f)
                        .clickable(enabled = enabled) { onClick(ClickBoxPosition.Bottom(it)) }
                )
            }
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(32.dp)
                    .background(Color.Red.copy(alpha = 0.5f))
                    .clickable(enabled = enabled) { onClick(ClickBoxPosition.Bottom(cardsInHandAmount - 1)) }
            )
        }
    }
}

@Preview
@Composable
fun PlayTableScreenPreview() {
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
        PlayTableScreen(playTableState, false) {}
    }
}
