package com.hanas.addy.view.gameSession

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.hanas.addy.model.PlayCardStack
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

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

    fun joinGameSession(code: String) = callbackFlow {
        functions.getHttpsCallable("join_game_session").call(mapOf("code" to code))
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


    fun getGameSessionFlow(id: String) = firestore.collection("gameSessions").document(id).snapshots().map {
        val cardStackDocument = it.get("cardStack") as? DocumentReference
        val selected = cardStackDocument?.snapshots()?.first()?.toObject<PlayCardStack>()
        it.toObject<GameSessionStateResponse>()?.toDomain(cardStacks = selected)
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