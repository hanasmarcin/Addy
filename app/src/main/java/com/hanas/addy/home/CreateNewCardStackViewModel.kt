package com.hanas.addy.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import coil.ImageLoader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CreateNewCardStackViewModel(
    private val geminiRepository: GeminiRepository,
    private val imageLoader: ImageLoader
) : ViewModel() {
    val photoUrisFlow: StateFlow<List<Uri>>
        get() = _photoUrisFlow

    private val _photoUrisFlow = MutableStateFlow<List<Uri>>(emptyList())

    fun addPhoto(uri: Uri) {
        _photoUrisFlow.value = _photoUrisFlow.value.toMutableList().apply { add(uri) }
    }

    fun addAllPhotos(uris: Collection<Uri>) {
        _photoUrisFlow.value = _photoUrisFlow.value.toMutableList().apply { addAll(uris) }
    }

    fun generateStack() {
    }

}
