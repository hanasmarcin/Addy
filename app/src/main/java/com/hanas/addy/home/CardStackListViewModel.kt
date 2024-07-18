package com.hanas.addy.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await

class CardStackListViewModel(
    firestoreRepository: FirestoreRepository
) : ViewModel() {

    val cardStacksFlow = firestoreRepository.observePlayingCardStacksForUser()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

}

class FirestoreRepository {
    private val database by lazy { Firebase.firestore }
    private val auth by lazy { Firebase.auth }

    fun savePlayingCardStack(cardStack: PlayingCardStack) {
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

    suspend fun getPlayingCardStacksForUser(): List<PlayingCardStack> {
        val userId = auth.uid ?: throw Exception("User not authenticated")

        return try {
            val snapshot = database.collection(CARD_STACKS_COLLECTION_PATH)
                .whereEqualTo("createdBy", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject<PlayingCardStack>() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting documents", e)
            emptyList()
        }
    }

    fun observePlayingCardStacksForUser(): Flow<List<PlayingCardStack>> = callbackFlow {
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

                val cardStacks = snapshot?.documents?.mapNotNull {
                    it.toObject<PlayingCardStack>()?.copy(id = it.id)
                } ?: emptyList()
                trySend(cardStacks)
            }

        awaitClose { listenerRegistration.remove() }
    }

    suspend fun getPlayingCardStackById(id: String): PlayingCardStack {
        val response = database.collection(CARD_STACKS_COLLECTION_PATH).document(id).get().await()
        return response.toObject<PlayingCardStack>()?.copy(id = response.id) ?: throw Exception("Card stack not found")
    }

    companion object {
        private const val CARD_STACKS_COLLECTION_PATH = "cardStacks"
        private const val TAG = "FirestoreRepository"
    }
}