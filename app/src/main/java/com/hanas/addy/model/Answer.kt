package com.hanas.addy.model

import androidx.annotation.IntRange
import kotlinx.serialization.Serializable

@Serializable
data class PlayingCardStackGeminiResponse(
    val title: String,
    val greenName: String,
    val redName: String,
    val blueName: String,
    val cards: List<PlayingCardGeminiResponse>,
)

@Serializable
data class PlayingCardGeminiResponse(
    val question: String,
    val a: String,
    val b: String,
    val c: String,
    val d: String,
    val answer: String,
    val title: String,
    val description: String,
    val greenValue: Int,
    val redValue: Int,
    val blueValue: Int,
    val imagePrompt: String,
)


@Serializable
data class PlayingCardStack(
    val title: String = "",
    val cards: List<PlayingCard> = emptyList(),
    val createdBy: String? = null,
    val creationTimestamp: Long = System.currentTimeMillis(),
    val id: String? = null,
)

@Serializable
data class PlayingCard(
    val question: Question = Question(),
    val title: String = "",
    val description: String = "",
    val attributes: Attributes = Attributes(Attribute(), Attribute(), Attribute())
)

@Serializable
data class Question(
    val text: String = "",
    val a: String = "",
    val b: String = "",
    val c: String = "",
    val d: String = "",
    val answer: Answer = Answer.A
)

@Serializable
enum class Answer(val value: String) {
    A("a"), B("b"), C("c"), D("d")
}

@Serializable
data class Attributes(
    val green: Attribute = Attribute(),
    val blue: Attribute = Attribute(),
    val red: Attribute = Attribute()
)

@Serializable
data class Attribute(
    val name: String = "",
    @IntRange(from = 1, to = 10) val value: Int = 1
)