package com.hanas.addy.view.gameSession

import android.util.Log
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ktx.database
import com.google.firebase.database.snapshots
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.hanas.addy.model.PlayCardStack
import com.hanas.addy.model.PlayCardStackDTO
import com.hanas.addy.view.createNewCardStack.mapToPlayCardStack
import com.hanas.addy.view.gameSession.createNewSession.GameAction
import com.hanas.addy.view.gameSession.createNewSession.GameActionsBatchDTO
import com.hanas.addy.view.gameSession.createNewSession.toDTO
import com.hanas.addy.view.gameSession.createNewSession.toDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class GameSessionRepository {
    private val functions by lazy { Firebase.functions }
    private val firestore by lazy { Firebase.firestore }
    private val database by lazy { Firebase.database("https://addy-7f8ed236-default-rtdb.europe-west1.firebasedatabase.app/") }


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
            val cardStack = cardStackDocument?.snapshots()?.first()?.toObject<PlayCardStackDTO>()?.mapToPlayCardStack(it.id)
            if (cardStack != null) {
                it.toObject<GameSessionStateDTO>()?.toDomain(cardStacks = cardStack)
            } else null
        }
        .catch {
            Log.e("HANASSS", it.stackTraceToString())
        }

    fun getGameActionsFlow(gameSessionId: String, isHandled: (String) -> Boolean) = actionsCollectionReference(gameSessionId)
        .snapshots
        .map {
            Log.d("HANASSS", "new game actions snapshot")
            it.children.mapNotNull { document ->
                val key = document.key
                if (key != null && isHandled(key).not()) {
                    document.getValue(GameActionsBatchDTO::class.java)?.toDomain(key)
                } else null
            }.sortedBy { it.timestamp }
        }

    private fun actionsCollectionReference(gameSessionId: String) = database.getReference("gameSessions/$gameSessionId/actions")

    fun startGame(gameSessionId: String) = functions.getHttpsCallable("start_game")
        .call(mapOf("gameSessionId" to gameSessionId))
        .asFlow()
        .map { result ->
            //result.data as? String ?: throw Exception("data is null")
        }

    fun sendSingleAction(action: GameAction, gameSessionId: String) = actionsCollectionReference(gameSessionId).push().apply {
        setValue(GameActionsBatchDTO(items = listOf(action.toDTO())))
    }.key

    suspend fun finishAnsweringQuestion(gameSessionId: String, cardId: Long, isAnswerCorrect: Boolean, answerTimeInMs: Long): AnswerScoreResult {
        val args = mapOf(
            "cardId" to cardId,
            "gameSessionId" to gameSessionId,
            "isAnswerCorrect" to isAnswerCorrect,
            "answerTimeInMs" to answerTimeInMs
        )
        val result = functions.getHttpsCallable("answer_question").call(args).await()
        val data = result.data as HashMap<*, *>
        return AnswerScoreResult(
            greenBooster = data["greenBooster"] as Int,
            redBooster = data["redBooster"] as Int,
            blueBooster = data["blueBooster"] as Int,
        )
    }

    suspend fun selectActiveAttribute(gameSessionId: String, attribute: String) {
        val args = mapOf(
            "gameSessionId" to gameSessionId,
            "attribute" to attribute,
        )
        functions.getHttpsCallable("select_attribute").call(args).await()
    }
}

data class AnswerScoreResult(
    val greenBooster: Int = 0,
    val redBooster: Int = 0,
    val blueBooster: Int = 0,
)


fun GameSessionStateDTO.toDomain(cardStacks: PlayCardStack) =
    if (startGameTimestamp != null && players.isNotEmpty() && unusedStack.isNotEmpty()) {
        GameSessionState.GameInProgress(
            cardStackInGame = cardStacks,
            startGameTimestamp = startGameTimestamp.toDate(),
            unusedStack = unusedStack,
            players = players.map {
                Player(
                    id = it.id,
                    displayName = it.displayName,
                    invitationStatus = it.invitationStatus.toInvitationStatus() ?: PlayerInvitationState.WAITING_FOR_RESPONSE
                )
            }
        )
    } else if (inviteCode != null) {
        GameSessionState.WaitingForPlayers(
            inviteCode = inviteCode,
            cardStackInGame = cardStacks,
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
    } else null


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
