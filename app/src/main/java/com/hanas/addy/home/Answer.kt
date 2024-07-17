package com.hanas.addy.home

import kotlinx.serialization.Serializable

@Serializable
data class PlayingCard(
    val question: String,
    val A: String,
    val B: String,
    val C: String,
    val D: String,
    val answer: String,
    val title: String,
    val description: String,
    val attributes: Attributes
)

@Serializable
data class Attributes(
    val green: Attribute,
    val blue: Attribute,
    val red: Attribute
)

@Serializable
data class Attribute(
    val name: String,
    val value: Int
)