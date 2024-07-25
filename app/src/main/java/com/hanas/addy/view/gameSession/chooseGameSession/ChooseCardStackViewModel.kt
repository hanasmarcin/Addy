package com.hanas.addy.view.gameSession.chooseGameSession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanas.addy.ui.NavAction
import com.hanas.addy.view.cardStackList.CardStackRepository
import com.hanas.addy.view.gameSession.GameSessionRepository
import com.hanas.addy.view.gameSession.createNewSession.CreateNewGameSession
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChooseCardStackViewModel(
    cardStackRepository: CardStackRepository,
    private val repository: GameSessionRepository
) : ViewModel(), NavigationRequester by NavigationRequester() {

    val cardStacksFlow = cardStackRepository.observePlayCardStacksForUser()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun onChooseCardStack(id: String) {
        viewModelScope.launch {
            repository.createGameSession(id).collect { newGameSessionId ->
                requestNavigation(
                    CreateNewGameSession(
                        newGameSessionId,
                        id
                    )
                )
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