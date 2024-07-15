package com.hanas.addy.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.hanas.addy.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}

fun requestCameraPermission(
    context: Context,
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    getPhotoFromCamera: () -> Unit
) {
    val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
        // Open camera because permission is already granted
        getPhotoFromCamera()
    } else {
        // Request a permission
        launcher.launch(permission)
    }
}

//@Composable
//fun rememberImagePickerHelper(): ImageMediaHelper {
//    val context = LocalContext.current
//    val photoUris = rememberSaveable { mutableStateListOf<Uri>() }
//    var nextUriForCamera by rememberSaveable { mutableStateOf<Uri?>(null) }
//    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
//        if (success) {
//            nextUriForCamera?.let { photoUris.add(it) }
//            nextUriForCamera = null
//        } else {
//            nextUriForCamera = null
//        }
//    }
//    val pickMultipleMediaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
//        if (uris.isNotEmpty()) {
//            // Multiple images selected
//        } else {
//            // No images selected
//        }
//    }
//
//    fun getPhotoFromCamera() {
//        val file = context.createImageFile()
//        nextUriForCamera = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.fileprovider", file)
//        nextUriForCamera?.let {
//            takePictureLauncher.launch(it)
//        }
//    }
//
//    fun getPhotosFromPicker() {
//        pickMultipleMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//    }
//    return ImageMediaHelper(context, takePictureLauncher, pickMultipleMediaLauncher)
//}

@Composable
fun rememberCameraHelper(): CameraHelper {
    val context = LocalContext.current
    val photoUris = remember { mutableStateListOf<Uri>() }
    var nextUriForCamera by rememberSaveable { mutableStateOf<Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            nextUriForCamera?.let { photoUris.add(it) }
            nextUriForCamera = null
        } else {
            nextUriForCamera = null
        }
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getPhotoFromCamera(context, takePictureLauncher) { uri ->
                nextUriForCamera = uri
            }
        } else {
            // Show dialog
        }
    }

    return remember(context, photoUris, takePictureLauncher) {
        object : CameraHelper {
            override val photoUris = photoUris
            override fun takeNewCameraPhoto() {
                requestCameraPermission(context, Manifest.permission.CAMERA, launcher) {
                    getPhotoFromCamera(context, takePictureLauncher) { uri ->
                        nextUriForCamera = uri
                    }
                }
            }

            override fun removeCameraPhoto(uri: Uri) {
                photoUris.remove(uri)
            }
        }
    }
}

private fun getPhotoFromCamera(
    context: Context,
    takePictureLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    onFileCreated: (Uri) -> Unit
) {
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.fileprovider", file)
    onFileCreated(uri)
    takePictureLauncher.launch(uri)
}

interface CameraHelper {
    val photoUris: List<Uri>
    fun takeNewCameraPhoto()
    fun removeCameraPhoto(uri: Uri)
}