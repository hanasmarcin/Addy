package com.hanas.addy.view.gameSession

import android.util.Log
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.hanas.addy.model.PlayCardStack
import com.hanas.addy.view.gameSession.createNewSession.GameActionsBatchDTO
import com.hanas.addy.view.gameSession.createNewSession.toDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class GameSessionRepository {
    private val functions by lazy { Firebase.functions }
    private val firestore by lazy { Firebase.firestore }

    fun createGameSession(selectedCardStackId: String) = callbackFlow {
        functions
            .getHttpsCallable("create_game_session")
            .call(mapOf("cardStackId" to selectedCardStackId))
            .continueWith { task ->
                try {
                    val result = task.result ?: throw Exception("result is null")
                    val data = result.data as? String ?: throw Exception("data is null")
                    trySend(data)
                } catch (e: Throwable) {
                    Log.e("HANASSS", e.stackTraceToString())
                }
            }
        awaitClose {
            this.cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun joinGameSession(code: String) = functions.getHttpsCallable("join_game_session")
        .call(mapOf("code" to code))
        .asFlow()
        .map { result ->
            result.data as? String ?: throw Exception("data is null")
        }


    fun getGameSessionFlow(id: String) = firestore.collection("gameSessions")
        .document(id)
        .snapshots()
        .map {
            val cardStackDocument = it.get("cardStack") as? DocumentReference
            val selected = cardStackDocument?.snapshots()?.first()?.toObject<PlayCardStack>()
            it.toObject<GameSessionStateResponse>()?.toDomain(cardStacks = selected)
        }

    fun getGameActionsFlow(gameSessionId: String, isHandled: (String) -> Boolean) = firestore
        .collection("gameSessions")
        .document(gameSessionId)
        .collection("actions")
        .snapshots()
        .map {
            Log.d("HANASSS", "new game actions snapshot")
            it.documents.mapNotNull { document ->
                if (isHandled(document.id).not()) {
                    document.toObject<GameActionsBatchDTO>()?.toDomain(document.id)
                } else null
            }
        }

    fun startGame(gameSessionId: String) = functions.getHttpsCallable("start_game")
        .call(mapOf("gameSessionId" to gameSessionId))
        .asFlow()
        .map { result ->
            result.data as? String ?: throw Exception("data is null")
        }
}


fun GameSessionStateResponse.toDomain(cardStacks: PlayCardStack?) = inviteCode?.let {
    GameSessionState(
        inviteCode = it,
        cardStack = cardStacks,
        players = players.mapNotNull { player ->
            player.invitationStatus.toInvitationStatus()?.let { invitation ->
                Player(
                    id = player.id,
                    displayName = player.displayName,
                    invitationStatus = invitation
                )
            }
        }
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Task<T>.asFlow(): Flow<T> = callbackFlow {
    val cancellationTokenSource = CancellationTokenSource()
    try {
        val result = await(cancellationTokenSource)
        trySend(result)
    } catch (e: Throwable) {
        close(e) // Close the flow with the error
    }
    awaitClose {
        cancellationTokenSource.cancel()
    }
}
