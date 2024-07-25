package com.hanas.addy.view.gameSession.createNewSession

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hanas.addy.repository.gemini.samplePlayCardStack
import com.hanas.addy.ui.NavScreen
import com.hanas.addy.ui.components.AppListItem
import com.hanas.addy.ui.components.AppScaffold
import com.hanas.addy.ui.components.PrimaryButton
import com.hanas.addy.ui.components.itemsWithPosition
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.gameSession.GameSessionState
import com.hanas.addy.view.gameSession.Player
import com.hanas.addy.view.gameSession.PlayerInvitationState
import com.hanas.addy.view.home.NavigationHandler
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel

@Serializable
data class CreateNewGameSession(
    val gameSessionId: String,
    val selectedCardStackId: String?
) : NavScreen

fun NavGraphBuilder.createNewSessionComposable(navHandler: NavigationHandler) {
    composable<CreateNewGameSession> {
        val viewModel: CreateNewGameSessionViewModel = koinNavViewModel()
        val state by viewModel.gameSessionStateFlow.collectAsState()
        CreateNewSessionScreen(state, navHandler)
    }
}

@Composable
fun CreateNewSessionScreen(state: GameSessionState, navHandler: NavigationHandler) {
    AppScaffold(
        navHandler = navHandler,
        topBarTitle = { Text("Create New Table") },
        bottomBar = {
            Surface(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 20.dp)
            ) {
                PrimaryButton(onClick = { }) { Text("Start game") }
            }
        }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Invite code", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.size(8.dp))
            Card {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, CenterHorizontally)) {
                    state.inviteCode.forEach { digit ->
                        Text(
                            digit.toString(),
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(12.dp),
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(Modifier.size(16.dp))
            Text("Players", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.size(8.dp))
            Card {
                LazyColumn(Modifier.padding(16.dp)) {
                    itemsWithPosition(state.players) { player, position, _ ->
                        AppListItem(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            onClick = {},
                            position = position,
                            trailingContent = {
                                when (player.invitationStatus) {
                                    PlayerInvitationState.WAITING_FOR_RESPONSE -> CircularProgressIndicator(
                                        Modifier
                                            .size(24.dp)
                                            .padding(2.dp),
                                        strokeWidth = 2.dp
                                    )
                                    PlayerInvitationState.ACCEPTED -> Icon(Icons.Default.Done, null)
                                    PlayerInvitationState.DECLINED -> Icon(Icons.Default.Clear, null)
                                }
                            }
                        ) {
                            Text(player.displayName)
                        }
                    }
                }
            }
            Spacer(Modifier.size(16.dp))
            Text("Card stack", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.size(8.dp))
            LazyRow(contentPadding = PaddingValues(16.dp)) {
                items(state.cardStack?.cards ?: emptyList()) {
                    Card {
                        Text(it.title)
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun CreateNewTableScreenPreview() {
    AppTheme {
        CreateNewSessionScreen(
            GameSessionState(
                "123456",
                listOf(
                    Player("1", "Marcin Hanas", PlayerInvitationState.WAITING_FOR_RESPONSE),
                    Player("2", "Marcin Hanas", PlayerInvitationState.ACCEPTED),
                    Player("3", "Marcin Hanas", PlayerInvitationState.DECLINED)
                ),
                samplePlayCardStack,
            )
        ) {}
    }
}