package com.hanas.addy.view.gameSession

import com.hanas.addy.model.PlayCardStack
import kotlinx.serialization.Serializable

data class GameSessionState(
    val inviteCode: String,
    val players: List<Player>,
    val cardStack: PlayCardStack?
)

data class Player(
    val id: String,
    val displayName: String,
    val invitationStatus: PlayerInvitationState,
)


@Serializable
data class GameSessionStateDTO(
    val inviteCode: String? = null,
    val players: List<PlayerDTO> = emptyList(),
)

@Serializable
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