package com.hanas.addy.view.createNewCardStack

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


@Composable
fun rememberPhotoPickerHelper(onPhotosPicked: (List<Uri>) -> Unit): PhotoPickerHelper {
    val coroutineScope = rememberCoroutineScope()
    val pickMultipleMediaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.isNotEmpty()) {
            coroutineScope.launch {
                onPhotosPicked(uris)
            }
        } else {
            // No photos selected
        }
    }
    return remember(pickMultipleMediaLauncher) {
        PhotoPickerHelper {
            pickMultipleMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
}

fun interface PhotoPickerHelper {
    fun pickPhotos()
}
