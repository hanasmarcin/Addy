package com.hanas.addy.model

import androidx.annotation.IntRange
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayCardStackGeminiResponse(
    val title: String,
    val greenName: String,
    val redName: String,
    val blueName: String,
    val cards: List<PlayCardGeminiResponse>,
)

@Serializable
data class PlayCardGeminiResponse(
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
data class PlayCardStack(
    val title: String = "",
    val cards: List<PlayCardData> = emptyList(),
    val createdBy: String? = null,
    val creationTimestamp: Long = System.currentTimeMillis(),
    val id: String? = null,
)

@Serializable
data class PlayCardData(
    val id: Int = 0,
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
    @SerialName("a")
    A("a"),

    @SerialName("b")
    B("b"),

    @SerialName("c")
    C("c"),

    @SerialName("d")
    D("d")
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