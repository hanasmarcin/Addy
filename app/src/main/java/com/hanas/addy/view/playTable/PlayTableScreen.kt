package com.hanas.addy.view.playTable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.R
import com.hanas.addy.model.Answer
import com.hanas.addy.repository.gemini.samplePlayCardStack
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.drawPattern
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin
import com.hanas.addy.view.playTable.model.AttributesFace
import com.hanas.addy.view.playTable.model.CardCollection
import com.hanas.addy.view.playTable.model.CardSlot
import com.hanas.addy.view.playTable.model.PlayTableState
import com.hanas.addy.view.playTable.view.PlayTable
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
data class PlayTable(val gameSessionId: String) : NavScreen

fun NavGraphBuilder.playTableComposable() {
    composable<PlayTable> {
        val viewModel = koinNavViewModel<PlayTableViewModel>()
        val state by viewModel.playTableStateFlow.collectAsState()
        val connection by viewModel.connectionStatusFlow.collectAsState()
        PlayTableScreen(
            state,
            connection,
            viewModel::onClickAwayFromCloseUp,
            viewModel::onClickCard,
            viewModel::onSelectAnswer,
            viewModel::onSelectToBattle,
            viewModel::onStartAnswer,
            viewModel::onSelectAttribute,
        )
    }
}

@Composable
fun PlayTableScreen(
    state: PlayTableState,
    isConnected: Boolean,
    onClickAwayFromCloseUp: () -> Unit,
    onClickCard: (Long, ClickOrigin) -> Unit,
    onSelectAnswer: (Long, Answer) -> Unit,
    onSelectToBattle: (Long) -> Unit,
    onStartAnswer: (Long) -> Unit,
    onSelectAttribute: (Int) -> Unit,
) {

    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
    val start = systemBarsPadding.calculateStartPadding(LocalLayoutDirection.current) + 32.dp
    val end = systemBarsPadding.calculateEndPadding(LocalLayoutDirection.current) + 32.dp
    val top = systemBarsPadding.calculateTopPadding()
    val bottom = systemBarsPadding.calculateBottomPadding()

    Surface {
        PlayTable(
            state,
            Modifier
                .drawPattern(R.drawable.graph_paper, tint = Color.Black.copy(alpha = 0.05f))
                .fillMaxSize()
                .padding(start = start, end = end, top = top, bottom = bottom),
            onClickAwayFromCloseUp = onClickAwayFromCloseUp,
            onSelectAnswer = onSelectAnswer,
            onSelectToBattle = onSelectToBattle,
            onClickCard = onClickCard,
            onStartAnswer = onStartAnswer,
            onSelectAttribute = onSelectAttribute,
        )
        AnimatedVisibility(isConnected.not()) {
            Text("Connection lost",
                Modifier
                    .padding(16.dp)
                    .background(Color.Cyan))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PlayTableScreenPreview() {
    AppTheme {
        val cardStack = samplePlayCardStack.cards
        var playTableState by remember {
            mutableStateOf(
                PlayTableState(
                    emptyMap(),
                    CardCollection(List(6) { cardStack[it] }),
                    CardCollection(List(3) { cardStack[it + 6] }),
                    CardCollection(List(2) { cardStack[it + 6 + 3] }),
                    closeUp = CardSlot(cardStack[12], AttributesFace.StaticPreview)
                )
            )
        }
        PlayTableScreen(playTableState, false, {}, { _, _ -> }, { _, _ -> }, {}, {}) { }
    }
}
