package com.hanas.addy.view.cardStackList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.hanas.addy.model.PlayCardStack
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await

class CardStackListViewModel(
    playCardRepository: PlayCardRepository
) : ViewModel() {
    val cardStacksFlow = playCardRepository.observePlayCardStacksForUser()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}

class PlayCardRepository {
    private val database by lazy { Firebase.firestore }
    private val auth by lazy { Firebase.auth }

    fun savePlayCardStack(cardStack: PlayCardStack) {
        database.collection(CARD_STACKS_COLLECTION_PATH)
            .add(cardStack)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: ${it.id}")
            }
            .addOnFailureListener {
                Log.e(TAG, "Error adding document", it)
            }
            .addOnCanceledListener {
                Log.e(TAG, "Canceled adding document")
            }
    }

    suspend fun getPlayCardStacksForUser(): List<PlayCardStack> {
        val userId = auth.uid ?: throw Exception("User not authenticated")

        return try {
            val snapshot = database.collection(CARD_STACKS_COLLECTION_PATH)
                .whereEqualTo("createdBy", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull(::mapDocumentToPlayCardStack)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting documents", e)
            emptyList()
        }
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