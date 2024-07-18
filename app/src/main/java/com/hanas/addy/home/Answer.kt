package com.hanas.addy.home

import kotlinx.serialization.Serializable

@Serializable
data class PlayingCardStack(
    val title: String = "",
    val cards: List<PlayingCard> = emptyList(),
    val createdBy: String = "",
    val id: String? = null,
)

@Serializable
data class PlayingCard(
    val question: String = "",
    val A: String = "",
    val B: String = "",
    val C: String = "",
    val D: String = "",
    val answer: String = "",
    val title: String = "",
    val description: String = "",
    val attributes: Attributes = Attributes(Attribute(), Attribute(), Attribute())
)

@Serializable
data class Attributes(
    val green: Attribute = Attribute(),
    val blue: Attribute = Attribute(),
    val red: Attribute = Attribute()
)

@Serializable
data class Attribute(
    val name: String = "",
    val value: Int = 0
)