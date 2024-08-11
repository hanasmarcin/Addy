package com.hanas.addy.view.gameSession.chooseGameSession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanas.addy.model.DataHolder
import com.hanas.addy.model.PlayCardStack
import com.hanas.addy.ui.NavAction
import com.hanas.addy.view.cardStackList.CardStackRepository
import com.hanas.addy.view.gameSession.GameSessionRepository
import com.hanas.addy.view.gameSession.createNewSession.CreateNewGameSession
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChooseCardStackViewModel(
    cardStackRepository: CardStackRepository,
    private val repository: GameSessionRepository
) : ViewModel(), NavigationRequester by NavigationRequester() {

    private val creatingGameSessionStateFlow = MutableStateFlow<DataHolder<String>>(DataHolder.Idle())

    private val cardStacksFlow = cardStackRepository.observePlayCardStacksForUser()
        .filter { it.isNotEmpty() }
        .map<List<PlayCardStack>, DataHolder<List<PlayCardStack>>> { DataHolder.Success(it) }
        .catch { emit(DataHolder.Error(it)) }

    val stateFlow = creatingGameSessionStateFlow.combine(cardStacksFlow) { creatingGameSessionState, cardStacks ->
        creatingGameSessionState to cardStacks
    }.stateIn(viewModelScope, SharingStarted.Eagerly, DataHolder.Idle<String>() to DataHolder.Loading())

    fun onChooseCardStack(id: String) {
        creatingGameSessionStateFlow.value = DataHolder.Loading()
        viewModelScope.launch {
            repository.createGameSession(id).collect { newGameSessionId ->
                creatingGameSessionStateFlow.value = DataHolder.Success(newGameSessionId)
                requestNavigation(NavAction(CreateNewGameSession(newGameSessionId, id), closeCurrentActivity = true))
            }
        }
    }
}

interface NavigationRequester {
    val navigationRequestFlow: SharedFlow<NavAction>
    fun requestNavigation(screen: NavAction)

    companion object {
        private object DefaultImpl : NavigationRequester {
            override val navigationRequestFlow =
                MutableSharedFlow<NavAction>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

            override fun requestNavigation(screen: NavAction) {
                navigationRequestFlow.tryEmit(screen)
            }
        }

        operator fun invoke(): NavigationRequester = DefaultImpl
    }
}