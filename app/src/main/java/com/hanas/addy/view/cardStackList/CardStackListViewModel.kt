package com.hanas.addy.view.cardStackList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.hanas.addy.model.PlayCardStack
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class CardStackListViewModel(
    cardStackRepository: CardStackRepository
) : ViewModel() {

    val cardStacksFlow = cardStackRepository.observePlayCardStacksForUser()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

}

class CardStackRepository {
    private val database by lazy { Firebase.firestore }
    private val auth by lazy { Firebase.auth }

    fun savePlayCardStack(cardStack: PlayCardStack) = callbackFlow<Result<DocumentReference>> {
        val listener = database.collection(CARD_STACKS_COLLECTION_PATH)
            .add(cardStack)
            .addOnSuccessListener {
                trySend(Result.success(it))
                Log.d(TAG, "DocumentSnapshot added with ID: ${it.id}")
            }
            .addOnFailureListener {
                trySend(Result.failure(it))
                Log.e(TAG, "Error adding document", it)
            }
            .addOnCanceledListener {
                trySend(Result.failure(CancellationException("Adding document canceled")))
            }
        awaitClose { this.cancel() }
    }

    fun getPlayCardStack(cardStackId: String): Flow<PlayCardStack> {
        return database.collection(CARD_STACKS_COLLECTION_PATH)
            .document(cardStackId)
            .snapshots()
            .map {
                it.toObject<PlayCardStack>()
            }
            .filterNotNull()
            .take(1)
    }


    fun getPlayCardStacksForUser(): Flow<List<PlayCardStack>> {
        val userId = auth.uid ?: throw Exception("User not authenticated")
        return database.collection(CARD_STACKS_COLLECTION_PATH)
            .whereEqualTo("createdBy", userId)
            .snapshots()
            .map {
                it.documents.mapNotNull(::mapDocumentToPlayCardStack)
            }
            .filterNotNull()
            .take(1)
    }

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
    document.toObject<PlayCardStack>()?.copy(id = document.id)
}.getOrNull()