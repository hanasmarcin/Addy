package com.hanas.addy.view.gameSession.createNewSession

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hanas.addy.view.cardStackList.CardStackRepository
import com.hanas.addy.view.gameSession.GameSessionRepository
import com.hanas.addy.view.gameSession.GameSessionState
import com.hanas.addy.view.gameSession.toDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class CreateNewGameSessionViewModel(
    gameSessionRepository: GameSessionRepository,
    cardStackRepository: CardStackRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val gameSessionId by lazy { savedStateHandle.toRoute<CreateNewGameSession>().gameSessionId }
    val gameSessionStateFlow = gameSessionRepository
        .getGameSessionFlow(gameSessionId)
        .combine(cardStackRepository.getPlayCardStacksForUser()) { gameSession, cardStacks ->
            gameSession?.toDomain(cardStacks)
        }
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GameSessionState("", emptyList(), emptyList()))
}



