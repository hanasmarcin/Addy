package com.hanas.addy.home

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateNewCardStackViewModel(
    private val geminiRepository: GeminiRepository,
    private val imageLoader: ImageLoader
) : ViewModel() {
    val outputContentFlow = MutableStateFlow<String?>(null)
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
               val bitmaps = _photoUrisFlow.value.map { it.toBitmap() }
                val response = geminiRepository.generateContent(bitmaps, "Summarize in one sentence this collection of photos")
                response.text?.let { outputContent ->
                    outputContentFlow.value = outputContent
                }
            } catch (e: Exception) {
                Log.e("HANASSS", e.stackTraceToString())
            }
        }
    }

}
