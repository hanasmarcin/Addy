package com.hanas.addy.view.playTable

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hanas.addy.model.Answer
import com.hanas.addy.model.PlayCardData
import com.hanas.addy.view.gameSession.GameSessionRepository
import com.hanas.addy.view.gameSession.GameSessionState
import com.hanas.addy.view.gameSession.createNewSession.CardPosition
import com.hanas.addy.view.gameSession.createNewSession.GameAction
import com.hanas.addy.view.gameSession.createNewSession.GameActionsBatch
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin.CLOSE_UP
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin.OPPONENT_BATTLE_SLOT
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin.PLAYER_BATTLE_SLOT
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin.PLAYER_HAND
import com.hanas.addy.view.playTable.model.PlayCardContentUiState
import com.hanas.addy.view.playTable.model.PlayCardContentUiState.QuestionRace
import com.hanas.addy.view.playTable.model.PlayTableSegmentType
import com.hanas.addy.view.playTable.model.PlayTableState
import com.hanas.addy.view.playTable.model.PlayTableState.CardSlot
import com.hanas.addy.view.playTable.model.PlayTableState.Segment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PlayTableViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: GameSessionRepository,
) : ViewModel() {
    private val handledActions = mutableSetOf<String>()
    private val navArgs by lazy { savedStateHandle.toRoute<PlayTable>() }

    private val gameSession = repository.getGameSessionFlow(navArgs.gameSessionId)
        .filterNotNull()
        .take(1)


    private val gameActions = repository.getGameActionsFlow(navArgs.gameSessionId, ::isActionHandled)
        .filterNotNull()
        .onEach { batchActions ->
            handledActions.addAll(batchActions.map { it.actionId })
        }
    //.buffer()
//.shareIn(viewModelScope, SharingStarted.Eagerly, replay = 0)

    private fun isActionHandled(actionId: String) = actionId in handledActions

    val playTableStateFlow = MutableStateFlow(
        PlayTableState(
            unusedStack = Segment(emptyList()),
            playerHand = Segment(emptyList()),
            opponentHand = Segment(emptyList()),
        )
    )

    private var tableState: PlayTableState
        get() = playTableStateFlow.value
        set(value) {
            playTableStateFlow.value = value
        }

    val cards = mutableListOf<PlayCardData>()


    init {
        viewModelScope.launch {
            gameSession.flatMapConcat { gameSession ->
                handleGameSession(gameSession)
            }.collect { batchAction ->
                handleBatchAction(batchAction)
            }
        }
    }

    private suspend fun handleGameSession(gameSession: GameSessionState): Flow<GameActionsBatch> {
        cards.clear()
        cards.addAll(gameSession.cardStackInGame.cards)
        return if (cards.isNotEmpty()) {
            gameActions.flatMapConcat { batchActions -> batchActions.asFlow() }
        } else {
            emptyFlow()
        }
    }

    private suspend fun handleBatchAction(batchAction: GameActionsBatch) {
        batchAction.unitActions.forEach { action ->
            delay(1000)
            Log.d("HANASSS", "Performing action: $action")
            when (action) {
                is GameAction.MoveCard -> handleMoveCardAction(action)
                is GameAction.FinishAnsweringQuestion -> handleFinishAnsweringQuestion(action)
                is GameAction.StartAnsweringQuestion -> handleStartAnsweringQuestion(action)
                is GameAction.AddCard -> handleAddCardAction(action)
                is GameAction.QuestionRaceResult -> handleQuestionRaceResult(action)
                is GameAction.SelectActiveAttribute -> handleSelectActiveAttribute(action)
            }
        }
    }

    private fun handleSelectActiveAttribute(action: GameAction.SelectActiveAttribute) {
        Log.d("HANASSS", action.toString())
    }

    private fun handleQuestionRaceResult(action: GameAction.QuestionRaceResult) {
        Log.d("HANASSS", action.toString())
    }

    private fun handleMoveCardAction(action: GameAction.MoveCard) {
        val (removedCard, stateWithRemovedCard) = removeCardFromCurrentPlacement(action)
        val stateWithMovedCard = placeCardToTargetPlacement(action, removedCard, stateWithRemovedCard)
        tableState = stateWithMovedCard
    }

    private fun removeCardFromCurrentPlacement(action: GameAction.MoveCard): Pair<PlayCardData, PlayTableState> {
        return when (action.currentPlacement) {
            is CardPosition.InHand -> removeCardFromHand(action.currentPlacement, action.cardId)
            is CardPosition.OnBattleSlot -> TODO()
            is CardPosition.UnusedStack -> removeCardFromUnusedStack(action)
        }
    }

    private fun removeCardFromHand(currentPlacement: CardPosition.InHand, cardId: Long): Pair<PlayCardData, PlayTableState> {
        return if (currentPlacement.forPlayerId == Firebase.auth.currentUser?.uid) {
            tableState.playerHand.cards.first { it.id == cardId } to tableState.copy(
                playerHand = Segment(tableState.playerHand.cards.dropAt(currentPlacement.positionInSegment))
            )
        } else {
            tableState.opponentHand.cards.first { it.id == cardId } to tableState.copy(
                opponentHand = Segment(tableState.opponentHand.cards.dropAt(currentPlacement.positionInSegment))
            )
        }
    }

    private fun removeCardFromUnusedStack(action: GameAction.MoveCard): Pair<PlayCardData, PlayTableState> {
        val removedCard = tableState.unusedStack.cards.first { it.id == action.cardId }
        val id = tableState.unusedStack.cards.indexOf(removedCard)
        return removedCard to tableState.copy(
            unusedStack = Segment(tableState.unusedStack.cards.dropAt(id))
        )
    }

    private fun placeCardToTargetPlacement(
        action: GameAction.MoveCard,
        removedCard: PlayCardData,
        stateWithRemovedCard: PlayTableState
    ): PlayTableState {
        return when (action.targetPlacement) {
            is CardPosition.InHand -> placeCardInHand(action.targetPlacement, removedCard, stateWithRemovedCard)
            is CardPosition.OnBattleSlot -> TODO()
            is CardPosition.UnusedStack -> placeCardInUnusedStack(removedCard, stateWithRemovedCard)
        }
    }

    private fun placeCardInHand(
        targetPlacement: CardPosition.InHand,
        removedCard: PlayCardData,
        stateWithRemovedCard: PlayTableState
    ): PlayTableState {
        return if (targetPlacement.forPlayerId == Firebase.auth.currentUser?.uid) {
            stateWithRemovedCard.copy(
                playerHand = Segment(
                    stateWithRemovedCard.playerHand.cards + removedCard,
                    stateWithRemovedCard.playerHand.availableSlots + 1
                )
            )
        } else {
            stateWithRemovedCard.copy(
                opponentHand = Segment(
                    stateWithRemovedCard.opponentHand.cards + removedCard,
                    stateWithRemovedCard.opponentHand.availableSlots + 1
                )
            )
        }
    }

    private fun placeCardInUnusedStack(removedCard: PlayCardData, stateWithRemovedCard: PlayTableState): PlayTableState {
        return stateWithRemovedCard.copy(
            unusedStack = Segment(
                stateWithRemovedCard.unusedStack.cards + removedCard,
                stateWithRemovedCard.unusedStack.availableSlots + 1
            )
        )
    }

    private suspend fun handleFinishAnsweringQuestion(action: GameAction.FinishAnsweringQuestion) {
        tableState.opponentHand.cards.firstOrNullIndexed { it.id == action.cardId }?.let { (index, card) ->
            tableState = tableState.copy(
                opponentBattleSlot = CardSlot(
                    card,
                    PlayTableSegmentType.TOP_OPPONENT_HAND,
                    index,
                    contentState = PlayCardContentUiState.OpponentWaitingForAttributeBattle
                )
            )
        }
    }

    private suspend fun handleStartAnsweringQuestion(action: GameAction.StartAnsweringQuestion) {
        if (action.playerId == Firebase.auth.currentUser?.uid) return // Skip if our own event
        tableState.opponentHand.cards.firstOrNullIndexed { it.id == action.cardId }?.let { (index, card) ->
            tableState = tableState.copy(
                opponentBattleSlot = CardSlot(
                    card,
                    PlayTableSegmentType.TOP_OPPONENT_HAND,
                    index,
                    contentState = PlayCardContentUiState.OpponentAnswering
                )
            )
        }
    }

    private suspend fun handleAddCardAction(action: GameAction.AddCard) {
        tableState = PlayTableState(
            unusedStack = Segment(tableState.unusedStack.cards + cards.first { it.id == action.cardId }),
            playerHand = Segment(emptyList()),
            opponentHand = Segment(emptyList())
        )
    }


    fun onClickCard(cardId: Long, origin: ClickOrigin) {
        viewModelScope.launch {
            when (origin) {
                CLOSE_UP -> {
                    clearCloseUp()
                }
                PLAYER_HAND -> {
                    val position = tableState.playerHand.cards.indexOfFirst { it.id == cardId }
                    closeUpTheCardFromPlayerHand(position)
                }
                PLAYER_BATTLE_SLOT -> {
                    val cardSlot = tableState.playerBattleSlot ?: return@launch
                    closeUpFromSlot(cardSlot)
                }
                OPPONENT_BATTLE_SLOT -> {
                    val cardSlot = tableState.opponentBattleSlot ?: return@launch
                    closeUpFromSlot(cardSlot)
                }
                ClickOrigin.NOT_CLICKABLE -> {}
            }
        }
    }

    fun onClickAwayFromCloseUp() {
        clearCloseUp()
    }

    fun onSelectAnswer(cardId: Long, answer: Answer) {
        val answerTimeInMs = System.currentTimeMillis() - requireNotNull(answerStartTimestamp) { "onSelectAnswer before onStartAnswer" }
        viewModelScope.launch {
            tableState.closeUp?.takeIf { it.card.id == cardId }?.let { slot ->
                val isAnswerCorrect = slot.card.question.answer == answer
                val deferred = async { repository.finishAnsweringQuestion(navArgs.gameSessionId, cardId, isAnswerCorrect, answerTimeInMs) }

                tableState = tableState.copy(closeUp = slot.copy(contentState = QuestionRace.Result(answer, isAnswerCorrect)))
                delay(1500)
                val attributes = deferred.await()
                tableState = tableState.copy(
                    closeUp = slot.copy(
                        contentState = PlayCardContentUiState.AttributesDisplay.AddingBoost(
                            attributes?.red?.booster ?: 0,
                            attributes?.green?.booster ?: 0,
                            attributes?.blue?.booster ?: 0,
                        )
                    )
                )
                delay(1500)
                tableState = tableState.copy(
                    closeUp = null,
                    playerBattleSlot = slot.copy(
                        contentState = PlayCardContentUiState.AttributesDisplay.WaitingForAttributeBattle
                    )
                )
            }
        }
    }

    fun onSelectToBattle(cardId: Long) {
        viewModelScope.launch {
            tableState.closeUp?.takeIf { it.card.id == cardId }?.let { slot ->
                tableState = tableState.copy(closeUp = slot.copy(contentState = QuestionRace.Initial))
            }
            //TODO Send to firebase
        }
    }


    enum class ClickOrigin {
        NOT_CLICKABLE, CLOSE_UP, PLAYER_HAND, PLAYER_BATTLE_SLOT, OPPONENT_BATTLE_SLOT

    }

    private fun clearCloseUp() {
        tableState = tableState.copy(closeUp = null)
    }

    private fun closeUpFromSlot(slot: CardSlot) {
        tableState = tableState.copy(
            closeUp = slot
        )
    }

    private fun closeUpTheCardFromPlayerHand(position: Int) {
        val card = tableState.playerHand.cards[position]
        tableState = tableState.copy(closeUp = CardSlot(card, PlayTableSegmentType.PLAYER_HAND, position))
    }

    private fun moveCloseUpToNextCardInPlayerHand(): Boolean {
        tableState.closeUp?.let { closeUp ->
            if (closeUp.originSegment != PlayTableSegmentType.PLAYER_HAND || closeUp.positionWithinOriginSegment == tableState.playerHand.availableSlots - 1) return false
            val newPosition = closeUp.positionWithinOriginSegment + 1
            tableState = tableState.copy(closeUp = CardSlot(tableState.playerHand.cards[newPosition], PlayTableSegmentType.PLAYER_HAND, newPosition))
            return true
        } ?: return false
    }

    private fun moveCloseUpToPreviousCardInPlayerHand(): Boolean {
        tableState.closeUp?.let { closeUp ->
            if (closeUp.originSegment != PlayTableSegmentType.PLAYER_HAND || closeUp.positionWithinOriginSegment == 0) return false
            val newPosition = closeUp.positionWithinOriginSegment - 1
            tableState = tableState.copy(closeUp = CardSlot(tableState.playerHand.cards[newPosition], PlayTableSegmentType.PLAYER_HAND, newPosition))
            return true
        } ?: return false
    }

    private var answerStartTimestamp: Long? = null

    fun onStartAnswer(cardId: Long) {
        viewModelScope.launch {
            launch {
                val action = GameAction.StartAnsweringQuestion(cardId = cardId, playerId = Firebase.auth.uid ?: "", msDelay = 0)
                repository.sendSingleAction(action, navArgs.gameSessionId)
            }
            tableState.closeUp?.takeIf { it.card.id == cardId }?.let { slot ->
                tableState = tableState.copy(closeUp = slot.copy(contentState = QuestionRace.Answering))
                answerStartTimestamp = System.currentTimeMillis()
            }
            //TODO Send to firebase
        }
    }

}

private fun <E> List<E>.dropAt(position: Int) = take(position) + drop(position + 1)

inline fun <T> Iterable<T>.firstOrNullIndexed(predicate: (T) -> Boolean): Pair<Int, T>? =
    mapIndexed { index, t -> index to t }.firstOrNull { (_, t) -> predicate(t) }
