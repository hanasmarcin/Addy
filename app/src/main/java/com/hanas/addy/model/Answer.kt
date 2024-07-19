package com.hanas.addy.model

import androidx.annotation.IntRange
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayingCardStackGeminiResponse(
    val title: String,
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
    val greenName: String,
    val greenValue: Int,
    val redName: String,
    val redValue: Int,
    val blueName: String,
    val blueValue: Int,
)


@Serializable
data class PlayingCardStack(
    val title: String = "",
    val cards: List<PlayingCardData> = emptyList(),
    val createdBy: String? = null,
    val creationTimestamp: Long = System.currentTimeMillis(),
    val id: String? = null,
)

@Serializable
data class PlayingCardData(
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
    @SerialName("a") A("a"),
    @SerialName("b") B("b"),
    @SerialName("c") C("c"),
    @SerialName("d") D("d")
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