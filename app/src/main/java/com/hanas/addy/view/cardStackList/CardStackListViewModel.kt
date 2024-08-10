package com.hanas.addy.view.cardStackList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.hanas.addy.model.DataHolder
import com.hanas.addy.model.PlayCardStack
import com.hanas.addy.model.PlayCardStackDTO
import com.hanas.addy.view.createNewCardStack.mapToPlayCardStack
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await

class CardStackListViewModel(
    cardStackRepository: CardStackRepository
) : ViewModel() {

    val cardStacksFlow = cardStackRepository.observePlayCardStacksForUser()
        .map<List<PlayCardStack>, DataHolder<List<PlayCardStack>>> { DataHolder.Success(it) }
        .catch { emit(DataHolder.Error(it)) }
        .runningReduce { previous, current ->
            when (current) {
                is DataHolder.Success, is DataHolder.Idle -> current
                is DataHolder.Loading -> DataHolder.Loading(previous.data, previous.error)
                is DataHolder.Error -> DataHolder.Error(current.error, previous.data)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DataHolder.Loading())
}

class CardStackRepository {
    private val database by lazy { Firebase.firestore }
    private val auth by lazy { Firebase.auth }

    fun observePlayCardStacksForUser(): Flow<List<PlayCardStack>> = callbackFlow {
        val userId = auth.uid ?: run {
            close(Exception("User not authenticated"))
            return@callbackFlow
        }

        val listenerRegistration = database.collection(CARD_STACKS_COLLECTION_PATH)
            .whereEqualTo("createdBy", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val cardStacks = snapshot?.documents?.mapNotNull(::mapDocumentToPlayCardStack) ?: emptyList()
                trySend(cardStacks)
            }

        awaitClose { listenerRegistration.remove() }
    }

    suspend fun getPlayCardStackById(id: String): PlayCardStack {
        val response = database.collection(CARD_STACKS_COLLECTION_PATH).document(id).get().await()
        return mapDocumentToPlayCardStack(response) ?: throw Exception("Card stack not found")
    }

    companion object {
        private const val CARD_STACKS_COLLECTION_PATH = "cardStacks"
        private const val TAG = "FirestoreRepository"
    }
}

private fun mapDocumentToPlayCardStack(document: DocumentSnapshot) = runCatching {
    document.toObject<PlayCardStackDTO>()?.mapToPlayCardStack(document.id)
}.getOrNull()