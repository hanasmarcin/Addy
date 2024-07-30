package com.hanas.addy.view.playTable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.model.Answer
import com.hanas.addy.repository.gemini.samplePlayCardStack
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin
import com.hanas.addy.view.playTable.model.CardCollection
import com.hanas.addy.view.playTable.model.PlayTableState
import com.hanas.addy.view.playTable.view.PlayTable
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
data class PlayTable(val gameSessionId: String) : NavScreen

fun NavGraphBuilder.playTableComposable() {
    composable<PlayTable> {
        val viewModel = koinNavViewModel<PlayTableViewModel>()
        val state by viewModel.playTableStateFlow.collectAsState()
        PlayTableScreen(
            state,
            viewModel::onClickAwayFromCloseUp,
            viewModel::onClickCard,
            viewModel::onSelectAnswer,
            viewModel::onSelectToBattle,
            viewModel::onStartAnswer,
        )
    }
}

@Composable
fun PlayTableScreen(
    state: PlayTableState,
    onClickAwayFromCloseUp: () -> Unit,
    onClickCard: (Long, ClickOrigin) -> Unit,
    onSelectAnswer: (Long, Answer) -> Unit,
    onSelectToBattle: (Long) -> Unit,
    onStartAnswer: (Long) -> Unit,
) {
    Surface {
        PlayTable(
            state,
            Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .systemBarsPadding(),
            onClickAwayFromCloseUp = onClickAwayFromCloseUp,
            onSelectAnswer = onSelectAnswer,
            onSelectToBattle = onSelectToBattle,
            onClickCard = onClickCard,
            onStartAnswer = onStartAnswer
        )
    }
}

@Preview
@Composable
fun PlayTableScreenPreview2() {
    AppTheme {
        val cardStack = samplePlayCardStack.cards
        var playTableState by remember {
            mutableStateOf(
                PlayTableState(
                    CardCollection(emptyList()),
                    CardCollection(emptyList()),
                    CardCollection(emptyList()),
                )
            )
        }
        LaunchedEffect(Unit) {
            delay(100)
            for (i in 0..20) {
                playTableState = playTableState.copy(
                    deck = CardCollection(listOf(cardStack[i]) + playTableState.deck.cards, 20)
                )
                delay(1000)
            }
        }
        PlayTableScreen(playTableState, {}, { _, _ -> }, { _, _ -> }, {}) {}
    }
}

@Preview
@Composable
fun PlayTableScreenPreview() {
    AppTheme {
        val cardStack = samplePlayCardStack.cards
        var playTableState by remember {
            mutableStateOf(
                PlayTableState(
                    CardCollection(List(6) { cardStack[it] }),
                    CardCollection(List(3) { cardStack[it + 6] }),
                    CardCollection(List(2) { cardStack[it + 6 + 3] }),
                )
            )
        }
        PlayTableScreen(playTableState, {}, { _, _ -> }, { _, _ -> }, {}) {}
    }
}
