package com.hanas.addy.view.gameSession.chooseGameSession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanas.addy.model.DataHolder
import com.hanas.addy.view.gameSession.GameSessionRepository
import com.hanas.addy.view.gameSession.createNewSession.CreateNewGameSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class ChooseGameSessionViewModel(
    private val repository: GameSessionRepository
) : ViewModel(), NavigationRequester by NavigationRequester() {
    val state = MutableStateFlow<DataHolder<String>>(DataHolder.Idle())

    fun joinSession(code: String) {
        state.value = DataHolder.Loading(state.value.data, state.value.error)
        viewModelScope.launch {
            repository.joinGameSession(code).take(1)
                .catch {
                    state.value = DataHolder.Error(it)
                }
                .collect {
                    state.value = DataHolder.Success(it)
                    requestNavigation(CreateNewGameSession(it, null))
                }
        }
    }
}
