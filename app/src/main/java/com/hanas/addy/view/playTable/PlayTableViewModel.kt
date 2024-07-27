package com.hanas.addy.view.playTable

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hanas.addy.model.Answer
import com.hanas.addy.model.PlayCardData
import com.hanas.addy.view.gameSession.GameSessionRepository
import com.hanas.addy.view.gameSession.createNewSession.CardPosition
import com.hanas.addy.view.gameSession.createNewSession.GameAction
import com.hanas.addy.view.playTable.PlayCardContentUiState.QuestionRace
import com.hanas.addy.view.playTable.PlayTableState.CardSlot
import com.hanas.addy.view.playTable.PlayTableState.Segment
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin.CLOSE_UP
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin.OPPONENT_BATTLE_SLOT
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin.PLAYER_BATTLE_SLOT
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin.PLAYER_HAND
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class PlayTableViewModel(
    savedStateHandle: SavedStateHandle,
    repository: GameSessionRepository,
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
                cards.clear()
                gameSession.cardStack?.cards?.let {
                    cards.addAll(it)
                }
                if (cards.isNotEmpty()) gameActions.transform { backActions -> backActions.onEach { emit(it) } } else emptyFlow()
            }.collect { batchAction ->
                batchAction.unitActions.forEach { action ->
                    delay(1000)
                    Log.d("HANASSS", "Performing action: $action")
                    when (action) {
                        is GameAction.MoveCard -> {
                            val (removedCard, stateWithRemovedCard) = when (action.currentPlacement) {
                                is CardPosition.InHand -> tableState.playerHand.cards.first { it.id == action.cardId } to tableState.copy(
                                    playerHand = Segment(tableState.playerHand.cards.dropAt(action.currentPlacement.positionInSegment))
                                )
                                is CardPosition.OnBattleSlot -> TODO()
                                is CardPosition.UnusedStack -> {
                                    val removedCard = tableState.unusedStack.cards.first { it.id == action.cardId }
                                    val id = tableState.unusedStack.cards.indexOf(removedCard)
                                    removedCard to tableState.copy(
                                        unusedStack = Segment(tableState.unusedStack.cards.dropAt(id))
                                    )
                                }
                            }
                            val stateWithMovedCard = when (action.targetPlacement) {
                                is CardPosition.InHand -> stateWithRemovedCard.copy(
                                    playerHand = Segment(
                                        stateWithRemovedCard.playerHand.cards + removedCard,
                                        stateWithRemovedCard.playerHand.availableSlots + 1
                                    )
                                )
                                is CardPosition.OnBattleSlot -> TODO()
                                is CardPosition.UnusedStack -> stateWithRemovedCard.copy(
                                    unusedStack = Segment(
                                        stateWithRemovedCard.unusedStack.cards + removedCard,
                                        stateWithRemovedCard.unusedStack.availableSlots + 1
                                    )
                                )
                            }
                            tableState = stateWithMovedCard
                        }
                        is GameAction.FinishAnsweringQuestion -> {}
                        is GameAction.StartAnsweringQuestion -> {}
                        is GameAction.AddCard -> {
                            tableState = PlayTableState(
                                unusedStack = Segment(tableState.unusedStack.cards + cards.first { it.id == action.cardId }),
                                playerHand = Segment(emptyList()),
                                opponentHand = Segment(emptyList())
                            )
                        }
                    }
                }
            }
        }
    }

    private fun dealCardsAtStart() {
        viewModelScope.launch {
            (1..5).forEach { _ ->
                giveCardFromUnusedToPlayer()
                delay(510)
            }
            (1..5).forEach { _ ->
                giveCardFromUnusedToOpponent()
                delay(510)
            }
        }
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
            }
        }
    }

    fun onClickAwayFromCloseUp() {
        clearCloseUp()
    }

    fun onSelectAnswer(cardId: Long, answer: Answer) {
        viewModelScope.launch {
            tableState.closeUp?.takeIf { it.card.id == cardId }?.let { slot ->
                val isAnswerCorrect = slot.card.question.answer == answer
                tableState = tableState.copy(closeUp = slot.copy(contentState = QuestionRace.Result(answer, isAnswerCorrect)))
                delay(1500)
                tableState = tableState.copy(
                    closeUp = slot.copy(
                        contentState = if (isAnswerCorrect) PlayCardContentUiState.AttributesDisplay.AddingBoost(
                            Random.nextInt(0, 2),
                            Random.nextInt(0, 2),
                            Random.nextInt(0, 2)
                        ) else PlayCardContentUiState.AttributesDisplay.AddingBoost(
                            Random.nextInt(-2, 0),
                            Random.nextInt(-2, 0),
                            Random.nextInt(-2, 0)
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
            //TODO Send to firebase

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
        CLOSE_UP, PLAYER_HAND, PLAYER_BATTLE_SLOT, OPPONENT_BATTLE_SLOT

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

    private suspend fun giveCardFromUnusedToPlayer(position: Int = tableState.unusedStack.cards.lastIndex) {
        val targetCard = tableState.unusedStack.cards[position]
        tableState = tableState.copy(
            playerHand = Segment(tableState.playerHand.cards, tableState.playerHand.availableSlots + 1)
        )
        delay(550)
        tableState = tableState.copy(
            unusedStack = Segment(tableState.unusedStack.cards.dropAt(position), tableState.unusedStack.availableSlots - 1),
            playerHand = Segment(tableState.playerHand.cards + targetCard, tableState.playerHand.availableSlots)
        )
    }

    private suspend fun giveCardFromUnusedToOpponent(): Boolean {
        if (tableState.unusedStack.cards.isEmpty()) return false
        tableState = tableState.copy(
            opponentHand = Segment(tableState.opponentHand.cards, tableState.opponentHand.availableSlots + 1)
        )
        delay(550)
        tableState = tableState.copy(
            unusedStack = Segment(tableState.unusedStack.cards.dropLast(1), tableState.unusedStack.availableSlots - 1),
            opponentHand = Segment(
                tableState.opponentHand.cards + tableState.unusedStack.cards.last(),
                tableState.opponentHand.availableSlots
            )
        )
        return true
    }

    private var answerStartTimestamp: Long? = null

    fun onStartAnswer(cardId: Long) {
        viewModelScope.launch {
            tableState.closeUp?.takeIf { it.card.id == cardId }?.let { slot ->
                tableState = tableState.copy(closeUp = slot.copy(contentState = QuestionRace.Answering))
                answerStartTimestamp = System.currentTimeMillis()
            }
            //TODO Send to firebase
        }
    }
}

private fun <E> List<E>.dropAt(position: Int) = take(position) + drop(position + 1)

