package com.hanas.addy.view.gameSession.createNewSession

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.firebase.Timestamp
import com.hanas.addy.model.DataHolder
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
import com.hanas.addy.view.gameSession.chooseGameSession.observeNavigation
import com.hanas.addy.view.home.NavigationHandler
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel
import java.security.InvalidParameterException
import java.util.Date

@Serializable
data class CreateNewGameSession(
    val gameSessionId: String,
    val selectedCardStackId: String?
) : NavScreen

fun NavGraphBuilder.createNewSessionComposable(navHandler: NavigationHandler) {
    composable<CreateNewGameSession> {
        val viewModel: CreateNewGameSessionViewModel = koinNavViewModel()
        val state by viewModel.gameSessionStateFlow.collectAsState()
        viewModel.observeNavigation(navHandler)
        CreateNewSessionScreen(state, navHandler, viewModel::startGame)
    }
}

@Composable
fun CreateNewSessionScreen(state: DataHolder<GameSessionState>, navHandler: NavigationHandler, startGame: () -> Unit) {
    AppScaffold(
        navHandler = navHandler,
        topBarTitle = { Text("Create New Table") },
        bottomBar = {
            Surface(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 20.dp)
            ) {
                PrimaryButton(onClick = startGame) { Text("Start game") }
            }
        }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Invite code", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.size(8.dp))
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shimmerLoadingAnimation(state is DataHolder.Loading),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, CenterHorizontally)
                ) {
                    (state.data?.inviteCode ?: "000000").forEach { digit ->
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
                    itemsWithPosition(state.data?.players ?: emptyList()) { player, position, _ ->
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
                items(state.data?.cardStack?.cards ?: emptyList()) {
                    Card {
                        Text(it.title)
                    }
                }
            }
        }
    }
}


class GameSessionStateProvider : PreviewParameterProvider<DataHolder<GameSessionState>> {
    override val values = sequenceOf(
        DataHolder.Loading(),
        DataHolder.Success(
            GameSessionState(
                "123456",
                listOf(
                    Player("1", "Marcin Hanas", PlayerInvitationState.WAITING_FOR_RESPONSE),
                    Player("2", "Marcin Hanas", PlayerInvitationState.ACCEPTED),
                    Player("3", "Marcin Hanas", PlayerInvitationState.DECLINED)
                ),
                samplePlayCardStack,
            )
        ),
        DataHolder.Error(Throwable("Something went wrong"))
    )
}


@Composable
@Preview
fun CreateNewTableScreenPreview(@PreviewParameter(GameSessionStateProvider::class) state: DataHolder<GameSessionState>) {
    AppTheme {
        CreateNewSessionScreen(state, {}) {}
    }
}

data class GameActionsBatchDTO(
    val items: List<GameActionDTO> = emptyList(),
    val timestamp: Timestamp = Timestamp.now()
)

data class GameActionDTO(
    val type: String? = null,
    val cardId: Long? = null,
    val currentSegment: CardPositionDTO? = null,
    val targetSegment: CardPositionDTO? = null,
    val msDelay: Long = 0,
)

data class CardPositionDTO(
    val type: String? = null,
    val forPlayerId: String? = null,
    val positionInSegment: Int? = null,
)

data class GameActionsBatch(
    val unitActions: List<GameAction>,
    val actionId: String,
    val timestamp: Date,
)

sealed class GameAction(open val msDelay: Long) {
    data class AddCard(
        val cardId: Long,
        val targetPlacement: CardPosition,
        override val msDelay: Long,
    ) : GameAction(msDelay)

    data class MoveCard(
        val cardId: Long,
        val currentPlacement: CardPosition,
        val targetPlacement: CardPosition,
        override val msDelay: Long,
    ) : GameAction(msDelay)

    data class StartAnsweringQuestion(
        val cardId: Long,
        override val msDelay: Long
    ) : GameAction(msDelay)

    data class FinishAnsweringQuestion(
        val cardId: Long,
        override val msDelay: Long
    ) : GameAction(msDelay)
}

sealed class CardPosition {
    data class UnusedStack(val positionInSegment: Int) : CardPosition()
    data class InHand(val positionInSegment: Int, val forPlayerId: String) : CardPosition()
    data class OnBattleSlot(val forPlayerId: String) : CardPosition()
}


fun Modifier.shimmerLoadingAnimation(
    isLoading: Boolean,
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000,
): Modifier {
    return composed {
        val shimmerColors = listOf(
            Color.White.copy(alpha = 0.3f),
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 1.0f),
            Color.White.copy(alpha = 0.5f),
            Color.White.copy(alpha = 0.3f),
        )

        val transition = rememberInfiniteTransition(label = "")

        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = durationMillis,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Restart,
            ),
            label = "Shimmer loading animation",
        )
        if (isLoading) {
            this
                .background(
                    brush = Brush.linearGradient(
                        colors = shimmerColors,
                        start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
                        end = Offset(x = translateAnimation.value, y = angleOfAxisY),
                    ),
                )
                .graphicsLayer { alpha = 0f }
        } else this
    }
}


fun GameActionsBatchDTO.toDomain(actionId: String) = GameActionsBatch(
    unitActions = items.map { it.toDomain() },
    actionId = actionId,
    timestamp = timestamp.toDate()
)

fun GameActionDTO.toDomain(): GameAction {
    val cardId = requireNotNull(cardId)
    return when (type) {
        "add" -> GameAction.AddCard(
            cardId = cardId,
            targetPlacement = requireNotNull(targetSegment).toDomain(),
            msDelay = msDelay
        )
        "move" -> GameAction.MoveCard(
            cardId = cardId,
            currentPlacement = requireNotNull(currentSegment).toDomain(),
            targetPlacement = requireNotNull(targetSegment).toDomain(),
            msDelay = msDelay
        )
        "startAnsweringQuestion" -> GameAction.StartAnsweringQuestion(cardId, msDelay)
        "finishAnsweringQuestion" -> GameAction.FinishAnsweringQuestion(cardId, msDelay)
        else -> throw InvalidParameterException("Unknown player action type: $type")
    }
}

private fun CardPositionDTO.toDomain() = when (type) {
    "unusedStack" -> CardPosition.UnusedStack(requireNotNull(positionInSegment))
    "inHand" -> CardPosition.InHand(requireNotNull(positionInSegment), requireNotNull(forPlayerId))
    "onBattleSlot" -> CardPosition.OnBattleSlot(requireNotNull(forPlayerId))
    else -> throw InvalidParameterException("Unknown card placement type: $type")
}
