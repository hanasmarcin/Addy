package com.hanas.addy.view.createNewCardStack

import android.graphics.drawable.Drawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.launch


@Composable
fun rememberPhotoPickerHelper(onPhotosPicked: (List<Drawable>) -> Unit): PhotoPickerHelper {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val pickMultipleMediaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.isNotEmpty()) {
            coroutineScope.launch {
                val drawables = uris.mapNotNull {
                    val request = ImageRequest.Builder(context).data(it).build()
                    context.imageLoader.execute(request).drawable
                }
                onPhotosPicked(drawables)
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
