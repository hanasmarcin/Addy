package com.hanas.addy.view.playTable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.model.Answer
import com.hanas.addy.repository.samplePlayingCardStack
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.theme.AppTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
object PlayTable : NavScreen

fun NavGraphBuilder.playTableComposable() {
    composable<PlayTable> {
        val viewModel = koinNavViewModel<PlayTableViewModel>()
        val state by viewModel.playTableStateFlow.collectAsState()
        PlayTableScreen(
            state,
            viewModel::onClickAwayFromCloseUp,
            viewModel::onClickCard,
            viewModel::onSelectAnswer
        )
    }
}

@Composable
fun PlayTableScreen(
    state: PlayTableState,
    onClickAwayFromCloseUp: () -> Unit,
    onClickCard: (PlayingCardState) -> Unit,
    onSelectAnswer: (PlayingCardState, Answer) -> Unit,
) {
    Surface {
        PlayTable(
            state,
            Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .systemBarsPadding()
                .background(Color.Red),
            onClickAwayFromCloseUp = onClickAwayFromCloseUp,
            onSelectAnswer = onSelectAnswer,
            onClickUpdateState = onClickCard
        )
    }
}

@Preview
@Composable
fun PlayTableScreenPreview() {
    AppTheme {
        val cardStack = samplePlayingCardStack.cards
        val playTableState = PlayTableState(
            PlayTableState.Segment(List(6) { cardStack[it] }),
            PlayTableState.Segment(List(3) { cardStack[it + 6] }),
            PlayTableState.Segment(List(2) { cardStack[it + 6 + 3] }),
        )
        PlayTableScreen(playTableState, {}, {}, { _, _ -> })
    }
}
