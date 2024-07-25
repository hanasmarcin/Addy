package com.hanas.addy.view.gameSession.chooseGameSession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanas.addy.view.gameSession.GameSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class ChooseGameSessionViewModel(
    private val repository: GameSessionRepository
) : ViewModel() {
    val state = MutableStateFlow(ChooseGameSessionState(null))

    fun joinSession(code: String) {
        viewModelScope.launch {
            repository.joinGameSession(code).take(1).collect {
                state.value = state.value.copy(newGameSessionId = it)
            }
        }
    }
}
