package com.hanas.addy.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CardStackDetailViewModel(
    private val firestoreRepository: FirestoreRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val cardStackId = try {
        savedStateHandle.toRoute<CardStackDetail>().id
    } catch (e: Throwable) {
        throw IllegalArgumentException("Invalid card stack id", e)
    }

    val cardStack = MutableStateFlow<PlayingCardStack?>(null)

    init {
        viewModelScope.launch {
            val x = firestoreRepository.getPlayingCardStackById(cardStackId)
            cardStack.value = x
        }
    }
}
