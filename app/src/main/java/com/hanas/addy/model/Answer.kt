package com.hanas.addy.model

import androidx.annotation.IntRange
import com.google.firebase.Timestamp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.Date

@Serializable
data class PlayCardStackDTO(
    val title: String = "",
    val greenName: String = "",
    val redName: String = "",
    val blueName: String = "",
    val cards: List<PlayCardDataDTO> = emptyList(),
    @Transient val creationTimestamp: Timestamp = Timestamp.now(),
    val createdBy: String = "",
)

@Serializable
data class PlayCardDataDTO(
    val id: Long = -1,
    val question: String = "",
    val a: String = "",
    val b: String = "",
    val c: String = "",
    val d: String = "",
    val answer: String = "",
    val title: String = "",
    val description: String = "",
    val greenValue: Int = 0,
    val redValue: Int = 0,
    val blueValue: Int = 0,
    val imagePrompt: String = "",
    val imagePath: String? = null,
)


data class PlayCardStack(
    val title: String,
    val cards: List<PlayCardData>,
    val createdBy: String?,
    val creationTimestamp: Date,
    val id: String?,
)

@Serializable
data class PlayCardData(
    val id: Long,
    val question: Question,
    val title: String,
    val description: String,
    val attributes: Attributes,
    val imagePrompt: String,
    val imagePath: String?,
)

@Serializable
data class Question(
    val text: String,
    val a: String,
    val b: String,
    val c: String,
    val d: String,
    val answer: Answer
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
    val green: Attribute,
    val blue: Attribute,
    val red: Attribute
)

@Serializable
data class Attribute(
    val name: String = "",
    @IntRange(from = 1, to = 10) val value: Int = 1,
)