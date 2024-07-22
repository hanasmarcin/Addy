package com.hanas.addy.view.createNewCardStack

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hanas.addy.model.Answer
import com.hanas.addy.model.Attribute
import com.hanas.addy.model.Attributes
import com.hanas.addy.model.PlayingCardData
import com.hanas.addy.model.PlayingCardStack
import com.hanas.addy.model.PlayingCardStackGeminiResponse
import com.hanas.addy.model.Question
import com.hanas.addy.repository.GeminiRepository
import com.hanas.addy.view.cardStackList.FirestoreRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

sealed class DataHolder<T>(
    open val data: T?,
    open val error: Throwable?
) {
    class Idle<T> : DataHolder<T>(null, null)

    data class Loading<T>(
        val cachedData: T? = null,
        val cachedError: Throwable? = null
    ) : DataHolder<T>(cachedData, cachedError)

    data class Success<T>(
        override val data: T
    ) : DataHolder<T>(data, null)

    data class Error<T>(
        override val error: Throwable,
        val cachedData: T? = null
    ) : DataHolder<T>(cachedData, error)
}


class CreateNewCardStackViewModel(
    private val geminiRepository: GeminiRepository,
    private val firestoreRepository: FirestoreRepository,
) : ViewModel() {
    val cardStackFlow = MutableStateFlow<DataHolder<PlayingCardStack>>(DataHolder.Idle())
    val photoUrisFlow: StateFlow<List<Drawable>>
        get() = _photoUrisFlow

    private val _photoUrisFlow = MutableStateFlow<List<Drawable>>(emptyList())

    fun addPhoto(uri: Drawable) {
        _photoUrisFlow.value = _photoUrisFlow.value.toMutableList().apply { add(uri) }
    }

    fun addAllPhotos(uris: Collection<Drawable>) {
        _photoUrisFlow.value = _photoUrisFlow.value.toMutableList().apply { addAll(uris) }
    }

    fun deleteGeneratedStack() {
        cardStackFlow.value = DataHolder.Idle()
    }

    fun generateStack() {
        val errorHandler = CoroutineExceptionHandler { _, throwable ->
            Log.e("HANASSS", "Error generating stack", throwable)
            cardStackFlow.value = DataHolder.Error(throwable, cachedData = cardStackFlow.value.data)
        }
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            cardStackFlow.value = DataHolder.Loading(cachedData = cardStackFlow.value.data, cachedError = cardStackFlow.value.error)
            val bitmaps = _photoUrisFlow.value.map { it.toBitmap() }
            val response = geminiRepository.generateCardStack(bitmaps)
            val generatedText = response.text
            if (generatedText.isNullOrBlank()) throw Throwable("Wrong data")
            val cleanString =
                generatedText.replaceFirst("```json", "").replaceFirst("```", "").replace(Regex("(?<=:\\s?)\"(.*?)(?<!\\\\)\""), "\"$1\"")

            val playingCards = (try {
                Json.decodeFromString<PlayingCardStackGeminiResponse>(cleanString)
            } catch (e: Throwable) {
                Log.e("HANASSS", "Failed to parse JSON", e)
                val correctedResponse = geminiRepository.correctCardStackJsonResponse(bitmaps, cleanString, e.message ?: e.toString())
                val generatedCorrectedText = correctedResponse.text
                if (generatedCorrectedText.isNullOrBlank()) throw Throwable("Wrong data")
                val cleanCorrectedString = generatedText.replaceFirst("```json", "").replaceFirst("```", "").replace(Regex("(?<=:\\s?)\"(.*?)(?<!\\\\)\""), "\"$1\"")
                Json.decodeFromString<PlayingCardStackGeminiResponse>(cleanCorrectedString)
            }).mapToPlayingCardStack(Firebase.auth.currentUser?.uid)
            cardStackFlow.value = DataHolder.Success(playingCards)
            firestoreRepository.savePlayingCardStack(playingCards)
        }
    }
}

fun PlayingCardStackGeminiResponse.mapToPlayingCardStack(createdBy: String?) = PlayingCardStack(
    title = title,
    createdBy = createdBy,
    creationTimestamp = System.currentTimeMillis(),
    cards = cards.map {
        PlayingCardData(
            title = it.title,
            description = it.description,
            question = Question(
                text = it.question,
                a = it.a,
                b = it.b,
                c = it.c,
                d = it.d,
                answer = when (it.answer) {
                    "a" -> Answer.A
                    "b" -> Answer.B
                    "c" -> Answer.C
                    "d" -> Answer.D
                    else -> throw Throwable("Invalid answer")
                }
            ),
            attributes = Attributes(
                green = Attribute(greenName, it.greenValue),
                red = Attribute(redName, it.redValue),
                blue = Attribute(blueName, it.blueValue)
            )
        )
    }
)