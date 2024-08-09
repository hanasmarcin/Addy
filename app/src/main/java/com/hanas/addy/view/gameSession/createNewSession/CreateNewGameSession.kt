package com.hanas.addy.view.gameSession.createNewSession

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
import com.hanas.addy.ui.theme.AppColors
import com.hanas.addy.ui.theme.AppColors.blue
import com.hanas.addy.ui.theme.AppColors.containerFor
import com.hanas.addy.ui.theme.AppTheme
import com.hanas.addy.view.gameSession.GameSessionState
import com.hanas.addy.view.gameSession.Player
import com.hanas.addy.view.gameSession.PlayerInvitationState
import com.hanas.addy.view.gameSession.chooseGameSession.observeNavigation
import com.hanas.addy.view.home.NavigationHandler
import com.hanas.addy.view.playTable.model.AttributesFace
import com.hanas.addy.view.playTable.model.QuestionFace
import com.hanas.addy.view.playTable.view.Scrim
import com.hanas.addy.view.playTable.view.cardcontent.PlayCardAttributes
import com.hanas.addy.view.playTable.view.cardcontent.PlayCardQuestion
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.navigation.koinNavViewModel
import java.security.InvalidParameterException
import java.util.Date

@Serializable
data class CreateNewGameSession(
    val gameSessionId: String, val selectedCardStackId: String?
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
    var isCardPreviewEnabled by remember { mutableStateOf(false) }
    AppScaffold(navHandler = navHandler, topBarTitle = { Text("Create New Table") }, bottomBar = {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 20.dp)
                .navigationBarsPadding()
        ) {
            PrimaryButton(onClick = startGame) { Text("Start game") }
        }
    }) {
        Column(Modifier.padding(16.dp)) {
            InviteCode(state)
            Spacer(Modifier.size(16.dp))
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(containerFor(AppColors.yellow))
                    .padding(16.dp)
            ) {
                Text("Players", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.size(8.dp))
                LazyColumn {
                    itemsWithPosition(state.data?.players ?: emptyList()) { player, position, _ ->
                        AppListItem(position = position, trailingContent = {
                            when (player.invitationStatus) {
                                PlayerInvitationState.WAITING_FOR_RESPONSE -> CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(2.dp),
                                    strokeWidth = 2.dp
                                )
                                PlayerInvitationState.ACCEPTED -> Icon(Icons.Default.Done, null)
                                PlayerInvitationState.DECLINED -> Icon(Icons.Default.Clear, null)
                            }
                        },
                            color = AppColors.yellow,
                            enabled = true,
                            isLoading = false,
                            onClick = {}
                        ) {
                            Text(player.displayName)
                        }
                    }
                }
            }
            Spacer(Modifier.size(16.dp))
            Column(
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(containerFor(blue))
                    .padding(16.dp)
            ) {
                Text("Card stack", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.size(8.dp))
                PrimaryButton(onClick = {
                    isCardPreviewEnabled = true
                }, color = blue, modifier = Modifier.fillMaxWidth()) {
                    Text(state.data?.cardStackInGame?.title ?: "")
                }
            }
        }
    }
    Scrim(isCardPreviewEnabled, true) {
        isCardPreviewEnabled = false
    }
    AnimatedVisibility(
        isCardPreviewEnabled,
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .zIndex(1000f)
    ) {
        val PlayCards = state.data?.cardStackInGame?.cards.orEmpty()
        HorizontalPager(rememberPagerState { PlayCards.size }, contentPadding = PaddingValues(horizontal = 32.dp), pageSpacing = 16.dp) { page ->
            val card = PlayCards[page]
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                var rotated by remember { mutableStateOf(false) }
                val rotation by animateFloatAsState(
                    targetValue = if (rotated) 180f else 0f,
                    animationSpec = tween(500), label = ""
                )
                val isFrontVisible by remember {
                    derivedStateOf {
                        rotation < 90f
                    }
                }
                Card(
                    Modifier
                        .zIndex(1000f)
                        .graphicsLayer {
                            rotationY = rotation
                            cameraDistance = 8 * density
                        }
                        .clickable { rotated = !rotated },
                    shape = RoundedCornerShape(24.dp)
                ) {
                    if (isFrontVisible) {
                        PlayCardAttributes(
                            state = AttributesFace.StaticPreview,
                            card = card,
                            onSelectAttribute = {},
                            onSelectCardToBattle = {}
                        )
                    } else {
                        var questionState by remember<MutableState<QuestionFace>> { mutableStateOf(QuestionFace.Answering) }
                        PlayCardQuestion(
                            modifier = Modifier.graphicsLayer { scaleX = -1f },
                            state = questionState,
                            card = card,
                            startAnswering = {},
                            onSelectAnswer = {
                                questionState = QuestionFace.AnswerScored(it, it == card.question.answer)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun scrimSizeInPx() = with(LocalDensity.current) {
    IntSize(
        LocalConfiguration.current.screenWidthDp.dp.roundToPx(),
        LocalConfiguration.current.screenHeightDp.dp.roundToPx()
    )
}


@Composable
private fun InviteCode(state: DataHolder<GameSessionState>, color: Color = AppColors.orange) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(containerFor(color))
            .padding(16.dp)
    ) {
        Text("Invite code", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.size(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
                .padding(top = 2.dp, bottom = 8.dp, start = 4.dp, end = 4.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            Row(
                modifier = Modifier
                    .background(color)
                    .fillMaxWidth()
                    .shimmerLoadingAnimation(state is DataHolder.Loading),
                horizontalArrangement = Arrangement.spacedBy(2.dp, CenterHorizontally)
            ) {
                ((state.data as? GameSessionState.WaitingForPlayers)?.inviteCode ?: "000000").forEach { digit ->
                    Text(
                        digit.toString(),
                        Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(12.dp),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


class GameSessionStateProvider : PreviewParameterProvider<DataHolder<GameSessionState>> {
    override val values = sequenceOf(
        DataHolder.Loading(), DataHolder.Success<GameSessionState>(
            GameSessionState.WaitingForPlayers(
                "123456",
                listOf(
                    Player("1", "Marcin Hanas", PlayerInvitationState.WAITING_FOR_RESPONSE),
                    Player("2", "Marcin Hanas", PlayerInvitationState.ACCEPTED),
                    Player("3", "Marcin Hanas", PlayerInvitationState.DECLINED)
                ),
                samplePlayCardStack,
            )
        ), DataHolder.Error(Throwable("Something went wrong"))
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
    val items: List<GameActionDTO> = emptyList(), val timestamp: Timestamp = Timestamp.now()
)

data class GameActionDTO(
    val type: String? = null,
    val cardId: Long? = null,
    val playerId: String? = null,
    val attribute: String? = null,
    val currentSegment: CardPositionDTO? = null,
    val targetSegment: CardPositionDTO? = null,
    val winnerIds: List<String>? = null,
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
        val cardId: Long, val playerId: String, override val msDelay: Long
    ) : GameAction(msDelay)

    data class FinishAnsweringQuestion(
        val cardId: Long, val playerId: String, override val msDelay: Long
    ) : GameAction(msDelay)

    data class QuestionRaceResult(
        val cardId: Long, // Id of the winning card
        val playerId: String, // Id of the winning player, that will choose active attribute
        override val msDelay: Long
    ) : GameAction(msDelay)

    data class SelectActiveAttribute(
        val attribute: String, // Selected active attribute, "red", "green", "blue"
        override val msDelay: Long
    ) : GameAction(msDelay)

    class AttributeBattleResult(
        val winnerIds: List<String>, msDelay: Long
    ) : GameAction(msDelay)

    class SwapCard(
        val cardId: Long,
        val targetPlacement: CardPosition,
        override val msDelay: Long
    ) : GameAction(msDelay)
}

sealed class CardPosition {
    data class UnusedStack(val positionInSegment: Int?) : CardPosition()
    data class InHand(val positionInSegment: Int?, val forPlayerId: String) : CardPosition()
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
    unitActions = items.map { it.toDomain() }, actionId = actionId, timestamp = timestamp.toDate()
)

fun GameActionDTO.toDomain(): GameAction {
    return when (type) {
        "add" -> GameAction.AddCard(
            cardId = requireNotNull(cardId), targetPlacement = requireNotNull(targetSegment).toDomain(), msDelay = msDelay
        )
        "move" -> GameAction.MoveCard(
            cardId = requireNotNull(cardId),
            currentPlacement = requireNotNull(currentSegment).toDomain(),
            targetPlacement = requireNotNull(targetSegment).toDomain(),
            msDelay = msDelay
        )
        "swap" -> GameAction.SwapCard(
            cardId = requireNotNull(cardId),
            targetPlacement = requireNotNull(targetSegment).toDomain(),
            msDelay = msDelay
        )
        "startAnsweringQuestion" -> GameAction.StartAnsweringQuestion(requireNotNull(cardId), requireNotNull(playerId), msDelay)
        "finishAnsweringQuestion" -> GameAction.FinishAnsweringQuestion(requireNotNull(cardId), requireNotNull(playerId), msDelay)
        "questionRaceResult" -> GameAction.QuestionRaceResult(requireNotNull(cardId), requireNotNull(playerId), msDelay)
        "selectActiveAttribute" -> GameAction.SelectActiveAttribute(requireNotNull(attribute), msDelay)
        "attributeBattleResult" -> GameAction.AttributeBattleResult(requireNotNull(winnerIds), msDelay)
        else -> throw InvalidParameterException("Unknown player action type: $type")
    }
}

private fun CardPositionDTO.toDomain() = when (type) {
    "unusedStack" -> CardPosition.UnusedStack(requireNotNull(positionInSegment))
    "inHand" -> CardPosition.InHand(positionInSegment, requireNotNull(forPlayerId))
    "battleSlot" -> CardPosition.OnBattleSlot(requireNotNull(forPlayerId))
    else -> throw InvalidParameterException("Unknown card placement type: $type")
}

private fun CardPosition.toDTO() = when (this) {
    is CardPosition.InHand -> CardPositionDTO("inHand", forPlayerId, positionInSegment)
    is CardPosition.OnBattleSlot -> CardPositionDTO("battleSlot", forPlayerId)
    is CardPosition.UnusedStack -> CardPositionDTO("unusedStack", positionInSegment = positionInSegment)
}


fun GameAction.toDTO() = when (this) {
    is GameAction.AddCard -> GameActionDTO("add", cardId, targetSegment = targetPlacement.toDTO(), msDelay = msDelay)
    is GameAction.FinishAnsweringQuestion -> GameActionDTO("finishAnsweringQuestion", cardId, playerId, msDelay = msDelay)
    is GameAction.MoveCard -> GameActionDTO(
        "move", cardId, currentSegment = currentPlacement.toDTO(), targetSegment = targetPlacement.toDTO(), msDelay = msDelay
    )
    is GameAction.StartAnsweringQuestion -> GameActionDTO("startAnsweringQuestion", cardId, playerId, msDelay = msDelay)
    is GameAction.QuestionRaceResult -> GameActionDTO("questionRaceResult", cardId, playerId, msDelay = msDelay)
    is GameAction.SelectActiveAttribute -> GameActionDTO("selectActiveAttribute", attribute = attribute, msDelay = msDelay)
    is GameAction.AttributeBattleResult -> GameActionDTO("attributeBattleResult", winnerIds = winnerIds, msDelay = msDelay)
    is GameAction.SwapCard -> GameActionDTO("swap", cardId, targetSegment = targetPlacement.toDTO(), msDelay = msDelay)

}