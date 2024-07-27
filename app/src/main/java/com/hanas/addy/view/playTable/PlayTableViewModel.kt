package com.hanas.addy.view.playTable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanas.addy.model.Answer
import com.hanas.addy.repository.gemini.samplePlayCardStack
import com.hanas.addy.view.playTable.PlayCardContentUiState.QuestionRace
import com.hanas.addy.view.playTable.PlayTableState.CardSlot
import com.hanas.addy.view.playTable.PlayTableState.Segment
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin.CLOSE_UP
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin.OPPONENT_BATTLE_SLOT
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin.PLAYER_BATTLE_SLOT
import com.hanas.addy.view.playTable.PlayTableViewModel.ClickOrigin.PLAYER_HAND
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed class PlayTableAction {
    data class ClickOnCard(val cardId: String) : PlayTableAction()
    data object ExitCardCloseUp : PlayTableAction()
    data class SelectBattleCard(val cardId: String) : PlayTableAction()
    data object StartAnsweringQuestion : PlayTableAction()
    data class AnswerQuestion(val answer: Answer) : PlayTableAction()
}

sealed class QuestionRaceResult {
    data class Correct(val answeringTimeInMs: Long) : QuestionRaceResult()
    data object Incorrect : QuestionRaceResult()
}

sealed class RoundPhase(val userInputEnabled: Boolean) {
    data object FetchingInitialCards : RoundPhase(false)
    data object DealingCards : RoundPhase(false)
    data object UserSelectsBattleCard : RoundPhase(true)
    data object PreparingQuestion : RoundPhase(false)
    data object UserAnswersQuestion : RoundPhase(true)
    data object FinalizingQuestionAnswer : RoundPhase(false)
    data object WaitingForQuestionRaceResult : RoundPhase(false)
    data object ProcessingQuestionRaceResult : RoundPhase(false)
    data object UserChoosesBattleAttribute : RoundPhase(true)
    data object WaitingForAttributeBattleResult : RoundPhase(false)
    data object ProcessingBattleResult : RoundPhase(false)
}


private fun <T> List<T>.lastWithIndexOrNull(): Pair<T, Int>? {
    val last = lastOrNull() ?: return null
    return last to lastIndex
}

class PlayTableViewModel : ViewModel() {
    private val playCards = samplePlayCardStack.cards.take(15).shuffled()
    val playTableStateFlow = MutableStateFlow(
        PlayTableState(
            unusedStack = Segment(playCards.take(15)),
            playerHand = Segment(emptyList()),
            opponentHand = Segment(emptyList()),
        )
    )

    private var tableState: PlayTableState
        get() = playTableStateFlow.value
        set(value) {
            playTableStateFlow.value = value
        }


    init {
        dealCardsAtStart()
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

    fun onClickCard(cardId: Int, origin: ClickOrigin) {
        viewModelScope.launch {
            when (origin) {
                CLOSE_UP -> {
                    clearCloseUp()
                }
                PLAYER_HAND -> {
                    val position = tableState.playerHand.cards.indexOfFirst { it.id == cardId } ?: return@launch
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

    fun onSelectAnswer(cardId: Int, answer: Answer) {
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

    fun onSelectToBattle(cardId: Int) {
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

    fun onStartAnswer(cardId: Int) {
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

