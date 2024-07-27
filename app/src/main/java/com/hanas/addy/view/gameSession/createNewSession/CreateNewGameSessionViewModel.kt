package com.hanas.addy.view.gameSession.createNewSession

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hanas.addy.model.DataHolder
import com.hanas.addy.model.wrapInDataHolder
import com.hanas.addy.view.gameSession.GameSessionRepository
import com.hanas.addy.view.gameSession.chooseGameSession.NavigationRequester
import com.hanas.addy.view.playTable.PlayTable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CreateNewGameSessionViewModel(
    private val gameSessionRepository: GameSessionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(), NavigationRequester by NavigationRequester() {
    private val navArgs by lazy { savedStateHandle.toRoute<CreateNewGameSession>() }
    val gameSessionStateFlow = gameSessionRepository.getGameSessionFlow(navArgs.gameSessionId)
        .filterNotNull()
        .wrapInDataHolder()
        .stateIn(viewModelScope, SharingStarted.Eagerly, DataHolder.Loading())

    fun startGame() {
        viewModelScope.launch {
            gameSessionRepository
                .startGame(navArgs.gameSessionId)
                .catch { Log.e("HANASSS", it.stackTraceToString()) }
                .collect {
                    requestNavigation(PlayTable(navArgs.gameSessionId))
                }
        }
    }
}



