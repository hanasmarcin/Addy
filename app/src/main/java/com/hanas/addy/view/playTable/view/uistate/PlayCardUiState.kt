package com.hanas.addy.view.playTable.view.uistate

import com.hanas.addy.model.PlayCardData
import com.hanas.addy.view.playTable.model.PlayCardContentState

data class PlayCardUiState(
    val data: PlayCardData,
    val placement: PlayCardUiPlacement,
    val contentState: PlayCardContentState,
)