package com.hanas.addy.view.gameSession

import com.google.firebase.Timestamp
import com.hanas.addy.model.PlayCardData
import com.hanas.addy.model.PlayCardStack
import java.util.Date

sealed class GameSessionState(open val players: List<Player>, open val cardStackInGame: PlayCardStack) {
    data class WaitingForPlayers(
        val inviteCode: String,
        override val players: List<Player>,
        override val cardStackInGame: PlayCardStack,
    ) : GameSessionState(players, cardStackInGame)

    data class GameInProgress(
        override val players: List<Player>,
        override val cardStackInGame: PlayCardStack,
        val unusedStack: List<PlayCardData>,
        val startGameTimestamp: Date,
    ) : GameSessionState(players, cardStackInGame)
}

data class Player(
    val id: String,
    val displayName: String,
    val invitationStatus: PlayerInvitationState,
)


data class GameSessionStateDTO(
    val inviteCode: String? = null,
    val players: List<PlayerDTO> = emptyList(),
    val unusedStack: List<PlayCardData> = emptyList(),
    val startGameTimestamp: Timestamp? = null,
)

data class PlayerDTO(
    val id: String = "",
    val displayName: String = "",
    val invitationStatus: String = "",
)

enum class PlayerInvitationState {
    WAITING_FOR_RESPONSE, ACCEPTED, DECLINED
}

fun String.toInvitationStatus() = when (this) {
    "waiting_for_response" -> PlayerInvitationState.WAITING_FOR_RESPONSE
    "accepted" -> PlayerInvitationState.ACCEPTED
    "declined" -> PlayerInvitationState.DECLINED
    else -> null
}