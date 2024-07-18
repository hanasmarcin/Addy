package com.hanas.addy.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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
    private val database = Firebase.firestore

    fun savePlayingCardStack(cardStack: PlayingCardStack) {
        database.collection(CARD_STACKS_COLLECTION_PATH)
            .add(cardStack).addOnSuccessListener {
                Log.d("HANASSS", "DocumentSnapshot added with ID: ${it.id}")
            }.addOnFailureListener {
                Log.e("HANASSS", "Error adding document", it)
            }.addOnCanceledListener {
                Log.e("HANASSS", "Canceled adding document")
            }
    }

    suspend fun getPlayingCardStacksForUser(): List<PlayingCardStack> {
        val userId = FirebaseAuth.getInstance().uid ?: throw Exception("User not authenticated")

        return try {
            val snapshot = database.collection(CARD_STACKS_COLLECTION_PATH)
                .whereEqualTo("createdBy", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toObject<PlayingCardStack>()
            }
        } catch (e: Exception) {
            // Handle error, e.g. log it or rethrow
            emptyList()
        }
    }

    fun observePlayingCardStacksForUser(): Flow<List<PlayingCardStack>> = callbackFlow {
        val userId = FirebaseAuth.getInstance().uid ?: run {
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

                val cardStacks = snapshot?.documents?.mapNotNull { document ->
                    document.toObject<PlayingCardStack>()
                } ?: emptyList()
                trySend(cardStacks)
            }

        awaitClose { listenerRegistration.remove() }
    }

    companion object {
        const val CARD_STACKS_COLLECTION_PATH = "cardStacks"
    }
}