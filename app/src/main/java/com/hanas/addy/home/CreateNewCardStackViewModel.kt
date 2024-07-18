package com.hanas.addy.home

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateNewCardStackViewModel(
    private val geminiRepository: GeminiRepository,
    private val firestoreRepository: FirestoreRepository,
) : ViewModel() {
    val outputContentFlow = MutableStateFlow(PlayingCardStack("", emptyList(), ""))
    val photoUrisFlow: StateFlow<List<Drawable>>
        get() = _photoUrisFlow

    private val _photoUrisFlow = MutableStateFlow<List<Drawable>>(emptyList())

    fun addPhoto(uri: Drawable) {
        _photoUrisFlow.value = _photoUrisFlow.value.toMutableList().apply { add(uri) }
    }

    fun addAllPhotos(uris: Collection<Drawable>) {
        _photoUrisFlow.value = _photoUrisFlow.value.toMutableList().apply { addAll(uris) }
    }

    fun generateStack() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                val bitmaps = _photoUrisFlow.value.map { it.toBitmap() }
//                val response = geminiRepository.generateContent(bitmaps, "Summarize in one sentence this collection of photos")
//                response.text?.let { outputContent ->
//                    val cleanString = outputContent.replaceFirst("```json", "").replaceFirst("```", "")
//                    val playingCards = Json.decodeFromString<List<PlayingCard>>(cleanString)
//                    outputContentFlow.value = playingCards

                Firebase.auth.currentUser?.let {
                    outputContentFlow.value = PlayingCardStack(
                        title = "Abc",
                        cards = List(5) { samplePlayingCard.copy(title = it.toString()) },
                        createdBy = it.uid
                    )
                }
                firestoreRepository.savePlayingCardStack(outputContentFlow.value)
            } catch (e: Exception) {
                Log.e("HANASSS", e.stackTraceToString())
            }
        }
    }

}
