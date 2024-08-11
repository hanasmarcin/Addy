package com.hanas.addy.view.playTable

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hanas.addy.model.Answer
import com.hanas.addy.model.PlayCardData
import com.hanas.addy.ui.theme.AppColors.blue
import com.hanas.addy.ui.theme.AppColors.orange
import com.hanas.addy.ui.theme.AppColors.pink
import com.hanas.addy.ui.theme.AppColors.yellow
import com.hanas.addy.view.gameSession.GameSessionRepository
import com.hanas.addy.view.gameSession.GameSessionState
import com.hanas.addy.view.gameSession.Player
import com.hanas.addy.view.gameSession.createNewSession.CardPosition
import com.hanas.addy.view.gameSession.createNewSession.GameAction
import com.hanas.addy.view.gameSession.createNewSession.GameActionsBatch
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin.CLOSE_UP
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin.OPPONENT_BATTLE_SLOT
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin.PLAYER_BATTLE_SLOT
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin.PLAYER_HAND
import com.hanas.addy.view.playTable.model.AttributesFace
import com.hanas.addy.view.playTable.model.BackFace
import com.hanas.addy.view.playTable.model.CardCollection
import com.hanas.addy.view.playTable.model.CardSlot
import com.hanas.addy.view.playTable.model.PlayTableState
import com.hanas.addy.view.playTable.model.QuestionFace
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

enum class PositionOnTable(val color: Color) {
    TOP(pink), BOTTOM(orange), START(blue), END(yellow)
}

data class PlayerState(
    val id: String,
    val name: String,
    val points: Int = 0,
)

class PlayTableViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: GameSessionRepository,
) : ViewModel() {

    private val database by lazy { Firebase.database("https://addy-7f8ed236-default-rtdb.europe-west1.firebasedatabase.app") }
    private val handledActions = mutableSetOf<String>()
    private val navArgs by lazy { savedStateHandle.toRoute<PlayTable>() }
    val connectionStatusFlow = getUserConnectionStatusFlow()
        .onEach { isConnected ->
            if (isConnected) {
                updateConnectionInFirebase(navArgs.gameSessionId, requireNotNull(Firebase.auth.uid))
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private fun getUserConnectionStatusFlow() = callbackFlow {
        val connectedRef = database.getReference(".info/connected")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: true
                trySend(connected)
            }

            override fun onCancelled(error: DatabaseError) {
                // Close the flow if the listener is cancelled and report the error
                close(error.toException())
            }
        }

        connectedRef.addValueEventListener(listener)

        // Clean up the listener when the flow is closed
        awaitClose {
            connectedRef.removeEventListener(listener)
        }
    }.onCompletion {
        Log.d("HANASSS", "Connection flow completed")
    }.catch { e ->
        Log.w("HANASSS", "Connection flow encountered an error: ${e.message}")
    }

    private fun presenceReference(gameSessionId: String, userId: String) = database.getReference("gameSessions/$gameSessionId/presence/$userId")

    private fun updateConnectionInFirebase(gameSessionId: String, userId: String) {
        presenceReference(gameSessionId, userId).setValue(true) // Set presence to true on connect
        presenceReference(gameSessionId, userId).onDisconnect().setValue(false) // Remove presence on disconnect
    }

    override fun onCleared() {
        presenceReference(navArgs.gameSessionId, requireNotNull(Firebase.auth.uid)).setValue(false)
        super.onCleared()
    }

    private val gameSession = repository.getGameSessionFlow(navArgs.gameSessionId)
        .filterNotNull()
        .take(1)


    private val gameActions = repository.getGameActionsFlow(navArgs.gameSessionId, ::isActionHandled)
        .filterNotNull()
        .onEach { batchActions ->
            handledActions.addAll(batchActions.map { it.actionId })
        }

    private fun isActionHandled(actionId: String) = actionId in handledActions

    val playTableStateFlow = MutableStateFlow(
        PlayTableState(
            players = emptyMap(),
            deck = CardCollection(emptyList()),
            playerHand = CardCollection(emptyList()),
            opponentHand = CardCollection(emptyList()),
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
            }.flatMapConcat {
                it.unitActions.asFlow()
            }.collect { batchAction ->
                try {
                    handleBatchAction(batchAction)
                } catch (e: Exception) {
                    Log.e("HANASSS", "ERROR handling action: $batchAction", e)
                }
            }
        }
    }

    fun List<Player>.reorder(startPlayerId: String): List<Player> {
        val startIndex = indexOfFirst { it.id == startPlayerId }
        if (startIndex == -1) throw Exception("Player wasn't found")  // Return the original list if the player ID is not found

        // Split the list into two parts: from startIndex to end, and from 0 to startIndex
        val part1 = subList(startIndex, size)
        val part2 = subList(0, startIndex)

        // Concatenate the two parts to form the reordered list
        return part1 + part2
    }


    private fun handleGameSession(gameSession: GameSessionState): Flow<GameActionsBatch> {
        cards.clear()
        cards.addAll(gameSession.cardStackInGame.cards)
        val players = gameSession.players.reorder(Firebase.auth.currentUser?.uid ?: throw Exception("User not found")).take(4)
        tableState = tableState.copy(
            players =
            players.mapIndexed { index, player ->
                val position = when {
                    index == 0 -> PositionOnTable.BOTTOM
                    index == 1 && players.size > 2 -> PositionOnTable.START
                    index == 1 && players.size == 2 || index == 2 -> PositionOnTable.TOP
                    else -> PositionOnTable.END
                }
                position to PlayerState(player.id, player.displayName)
            }.toMap()
        )
        return if (cards.isNotEmpty()) {
            gameActions.flatMapConcat { batchActions -> batchActions.asFlow() }
        } else {
            emptyFlow()
        }
    }

    private suspend fun handleBatchAction(action: GameAction) {
        when (action) {
            is GameAction.AddCard -> handleAddCardAction(action)
            is GameAction.MoveCard -> handleMoveCardAction(action)
            is GameAction.SwapCard -> handleSwapCardAction(action)
            is GameAction.FinishAnsweringQuestion -> handleFinishAnsweringQuestion(action)
            is GameAction.StartAnsweringQuestion -> handleStartAnsweringQuestion(action)
            is GameAction.QuestionRaceResult -> handleQuestionRaceResult(action)
            is GameAction.SelectActiveAttribute -> handleSelectActiveAttribute(action)
            is GameAction.AttributeBattleResult -> handleAttributeBattleResult(action)
        }
    }

    private fun handleSwapCardAction(action: GameAction.SwapCard) {
        when (action.targetPlacement) {
            is CardPosition.InHand -> {

            }
            is CardPosition.OnBattleSlot -> {

            }
            is CardPosition.UnusedStack -> {
                action.targetPlacement.positionInSegment?.let { position ->
                    tableState = tableState.copy(
                        deck = tableState.deck.copy(
                            cards = tableState.deck.cards.subList(0, position) +
                                cards.first { it.id == action.cardId } +
                                tableState.deck.cards.subList(position + 1, tableState.deck.size)),
                    )
                }
            }
        }

    }

    private suspend fun handleAttributeBattleResult(action: GameAction.AttributeBattleResult) {
        val hasPlayerWon = Firebase.auth.currentUser?.uid in action.winnerIds
        val hasOpponentWon = (hasPlayerWon && action.winnerIds.size > 1) || (hasPlayerWon.not() && action.winnerIds.isNotEmpty())
        tableState = tableState.copy(
            closeUp = null,
            players = tableState.players.mapValues { (_, state) -> if (state.id in action.winnerIds) state.copy(points = state.points + 1) else state },
            playerBattleSlot = tableState.playerBattleSlot?.copy(contentState = AttributesFace.BattleResult(hasPlayerWon)),
            opponentBattleSlot = tableState.opponentBattleSlot?.copy(contentState = AttributesFace.BattleResult(hasOpponentWon))
        )
        delay(3000)
    }

    private suspend fun handleSelectActiveAttribute(action: GameAction.SelectActiveAttribute) {
        tableState.playerBattleSlot?.let {
            val updatedSlot = it.copy(contentState = AttributesFace.ActiveAttributeSelected(action.attribute))
            tableState = tableState.copy(
                playerBattleSlot = updatedSlot,
                closeUp = updatedSlot
            )
            delay(3000)
        }
    }

    private suspend fun handleQuestionRaceResult(action: GameAction.QuestionRaceResult) {
        if (Firebase.auth.currentUser?.uid == action.playerId) {
            delay(2000)
            val winningCard = tableState.playerBattleSlot?.card
            if (winningCard != null) {
                val newSlot = CardSlot(winningCard, contentState = AttributesFace.ChooseActiveAttribute)
                tableState = tableState.copy(
                    playerBattleSlot = newSlot,
                    closeUp = newSlot
                )
            }
            delay(3000)
        }
    }

    private suspend fun handleMoveCardAction(action: GameAction.MoveCard) {
        val (removedCard, stateWithRemovedCard) = removeCardFromCurrentPlacement(action.currentPlacement, action.cardId)
        val stateWithMovedCard = placeCardToTargetPlacement(action, removedCard, stateWithRemovedCard)
        tableState = stateWithMovedCard
        delay(600)
    }

    private fun removeCardFromCurrentPlacement(currentPlacement: CardPosition, cardId: Long): Pair<PlayCardData, PlayTableState> {
        return when (currentPlacement) {
            is CardPosition.InHand -> removeCardFromHand(currentPlacement, cardId)
            is CardPosition.OnBattleSlot -> removeCardFromBattleSlot(currentPlacement, cardId)
            is CardPosition.UnusedStack -> removeCardFromUnusedStack(cardId)
        }
    }

    private fun removeCardFromBattleSlot(currentPlacement: CardPosition.OnBattleSlot, cardId: Long): Pair<PlayCardData, PlayTableState> {
        return if (currentPlacement.forPlayerId == Firebase.auth.currentUser?.uid) {
            requireNotNull(tableState.playerBattleSlot?.card) to tableState.copy(
                playerHand = CardCollection(tableState.playerHand.cards.filter { it.id != cardId }),
                playerBattleSlot = null
            )
        } else {
            requireNotNull(tableState.opponentBattleSlot?.card) to tableState.copy(
                opponentHand = CardCollection(tableState.opponentHand.cards.filter { it.id != cardId }),
                opponentBattleSlot = null
            )
        }
    }

    private fun removeCardFromHand(currentPlacement: CardPosition.InHand, cardId: Long): Pair<PlayCardData, PlayTableState> {
        return if (currentPlacement.forPlayerId == Firebase.auth.currentUser?.uid) {
            tableState.playerHand.cards.first { it.id == cardId } to tableState.copy(
                playerHand = CardCollection(tableState.playerHand.cards.dropAt(requireNotNull(currentPlacement.positionInSegment)))
            )
        } else {
            tableState.opponentHand.cards.first { it.id == cardId } to tableState.copy(
                opponentHand = CardCollection(tableState.opponentHand.cards.dropAt(requireNotNull(currentPlacement.positionInSegment)))
            )
        }
    }

    private fun removeCardFromUnusedStack(cardId: Long): Pair<PlayCardData, PlayTableState> {
        val removedCard = tableState.deck.cards.first { it.id == cardId }
        val id = tableState.deck.cards.indexOf(removedCard)
        return removedCard to tableState.copy(
            deck = CardCollection(tableState.deck.cards.dropAt(id))
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
            is CardPosition.UnusedStack -> placeCardInUnusedStack(
                removedCard,
                stateWithRemovedCard,
                requireNotNull(action.targetPlacement.positionInSegment)
            )
        }
    }

    private fun placeCardInHand(
        targetPlacement: CardPosition.InHand,
        removedCard: PlayCardData,
        stateWithRemovedCard: PlayTableState
    ): PlayTableState {
        return if (targetPlacement.forPlayerId == Firebase.auth.currentUser?.uid) {
            stateWithRemovedCard.copy(
                playerHand = CardCollection(
                    stateWithRemovedCard.playerHand.cards + removedCard,
                    stateWithRemovedCard.playerHand.size + 1
                )
            )
        } else {
            stateWithRemovedCard.copy(
                opponentHand = CardCollection(
                    stateWithRemovedCard.opponentHand.cards + removedCard,
                    stateWithRemovedCard.opponentHand.size + 1
                )
            )
        }
    }

    private fun placeCardInUnusedStack(removedCard: PlayCardData, stateWithRemovedCard: PlayTableState, positionInSegment: Int): PlayTableState {
        return stateWithRemovedCard.copy(
            deck = CardCollection(
                stateWithRemovedCard.deck.cards.subList(0, positionInSegment) + removedCard + stateWithRemovedCard.deck.cards.subList(
                    positionInSegment,
                    stateWithRemovedCard.deck.size
                ),
                stateWithRemovedCard.deck.size + 1
            )
        )
    }

    private suspend fun handleFinishAnsweringQuestion(action: GameAction.FinishAnsweringQuestion) {
        tableState.opponentHand.cards.firstOrNull { it.id == action.cardId }?.let { card ->
            tableState = tableState.copy(
                opponentBattleSlot = CardSlot(card, BackFace.OpponentWaitingForAttributeBattle)
            )
        }
    }

    private suspend fun handleStartAnsweringQuestion(action: GameAction.StartAnsweringQuestion) {
        if (action.playerId == Firebase.auth.currentUser?.uid) return // Skip if our own event
        tableState.opponentHand.cards.firstOrNull { it.id == action.cardId }?.let { card ->
            tableState = tableState.copy(
                opponentBattleSlot = CardSlot(card, BackFace.OpponentAnswering)
            )
        }
    }

    private suspend fun handleAddCardAction(action: GameAction.AddCard) {
        tableState = tableState.copy(
            deck = CardCollection(tableState.deck.cards + cards.first { it.id == action.cardId }),
        )
        delay(600)
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

                tableState = tableState.copy(closeUp = slot.copy(contentState = QuestionFace.AnswerScored(answer, isAnswerCorrect)))
                val result = deferred.await()
                tableState = tableState.copy(
                    closeUp = slot.copy(
                        contentState = AttributesFace.AddingBoost(
                            result.redBooster,
                            result.greenBooster,
                            result.blueBooster,
                        )
                    )
                )
                delay(1500)
                tableState = tableState.copy(
                    closeUp = null,
                    playerBattleSlot = slot.copy(
                        contentState = AttributesFace.WaitingForActiveAttributeSelected
                    )
                )
            }
        }
    }

    fun onSelectToBattle(cardId: Long) {
        viewModelScope.launch {
            tableState.closeUp?.takeIf { it.card.id == cardId }?.let { slot ->
                tableState = tableState.copy(closeUp = slot.copy(contentState = QuestionFace.ReadyToAnswer))
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
        val contentState = if (tableState.playerBattleSlot == null) AttributesFace.ChoosingToBattle else AttributesFace.StaticPreview
        tableState = tableState.copy(closeUp = CardSlot(card, contentState))
    }

//    private fun moveCloseUpToNextCardInPlayerHand(): Boolean {
//        tableState.closeUp?.let { closeUp ->
//            if (closeUp.ori != PlayTableSegmentType.PLAYER_HAND || closeUp.positionWithinOriginSegment == tableState.playerHand.availableSlots - 1) return false
//            val newPosition = closeUp.positionWithinOriginSegment + 1
//            tableState = tableState.copy(closeUp = CardSlot(tableState.playerHand.cards[newPosition], PlayTableSegmentType.PLAYER_HAND, newPosition))
//            return true
//        } ?: return false
//    }
//<
//    private fun moveCloseUpToPreviousCardInPlayerHand(): Boolean {
//        tableState.closeUp?.let { closeUp ->
//            if (closeUp.originSegment != PlayTableSegmentType.PLAYER_HAND || closeUp.positionWithinOriginSegment == 0) return false
//            val newPosition = closeUp.positionWithinOriginSegment - 1
//            tableState = tableState.copy(closeUp = CardSlot(tableState.playerHand.cards[newPosition], PlayTableSegmentType.PLAYER_HAND, newPosition))
//            return true
//        } ?: return false
//    }

    private var answerStartTimestamp: Long? = null

    fun onStartAnswer(cardId: Long) {
        viewModelScope.launch {
            launch {
                val action = GameAction.StartAnsweringQuestion(cardId = cardId, playerId = Firebase.auth.uid ?: "", msDelay = 0)
                repository.sendSingleAction(action, navArgs.gameSessionId)
            }
            tableState.closeUp?.takeIf { it.card.id == cardId }?.let { slot ->
                val newSlot = slot.copy(contentState = QuestionFace.Answering)
                tableState = tableState.copy(closeUp = newSlot, playerBattleSlot = newSlot)
                answerStartTimestamp = System.currentTimeMillis()
            }
        }
    }

    fun onSelectAttribute(attributeId: Int) {
        viewModelScope.launch {
            val attribute = when (attributeId) {
                0 -> "red"
                1 -> "green"
                else -> "blue"
            }
            repository.selectActiveAttribute(navArgs.gameSessionId, attribute)
        }
    }

}

private fun <E> List<E>.dropAt(position: Int) = take(position) + drop(position + 1)

inline fun <T> Iterable<T>.firstOrNullIndexed(predicate: (T) -> Boolean): Pair<Int, T>? =
    mapIndexed { index, t -> index to t }.firstOrNull { (_, t) -> predicate(t) }
