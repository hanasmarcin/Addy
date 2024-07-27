package com.hanas.addy.view.gameSession.createNewSession

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hanas.addy.view.gameSession.GameSessionRepository
import com.hanas.addy.view.gameSession.GameSessionState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CreateNewGameSessionViewModel(
    private val gameSessionRepository: GameSessionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val navArgs by lazy { savedStateHandle.toRoute<CreateNewGameSession>() }
    val gameSessionStateFlow: StateFlow<GameSessionState> = gameSessionRepository
        .getGameSessionFlow(navArgs.gameSessionId)
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Eagerly, GameSessionState("", emptyList(), null))

    fun startGame() {
        viewModelScope.launch {
            gameSessionRepository.startGame(navArgs.gameSessionId).catch { Log.e("HANASSS", it.stackTraceToString()) }.collect {
                Log.d("HANASSS", "gameSessionId: $it")
            }
        }
    }
}



