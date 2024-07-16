package com.hanas.addy.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember


@Composable
fun rememberPhotoPickerHelper(onPhotosPicked: (List<Uri>) -> Unit): PhotoPickerHelper {
    val pickMultipleMediaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.isNotEmpty()) {
            onPhotosPicked(uris)
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
