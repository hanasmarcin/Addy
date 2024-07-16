package com.hanas.addy.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.imageLoader
import coil.request.ImageRequest
import com.hanas.addy.BuildConfig
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun interface CameraHelper {
    fun takePhoto()
}

@Composable
fun rememberCameraHelper(onPhotoTaken: suspend (Drawable) -> Unit): CameraHelper {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var uriPhotoToCapture by rememberSaveable { mutableStateOf<Uri?>(null) }
    val capturePhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            uriPhotoToCapture?.let {
                coroutineScope.launch {
                    val request = ImageRequest.Builder(context).data(it).build()
                    val drawable = context.imageLoader.execute(request).drawable
                    if (drawable != null) {
                        onPhotoTaken(drawable)
                    } else {
                        uriPhotoToCapture = null
                    }
                }
            }
            uriPhotoToCapture = null
        } else {
            uriPhotoToCapture = null
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            capturePhotoLauncher.requestPhotoCapture(context) { uri ->
                uriPhotoToCapture = uri
            }
        } else {
            // Show dialog
        }
    }

    return remember(context, capturePhotoLauncher) {
        CameraHelper {
            permissionLauncher.requestCameraPermission(
                context = context,
                onPermissionGrantedAlready = {
                    capturePhotoLauncher.requestPhotoCapture(context) { uri ->
                        uriPhotoToCapture = uri
                    }
                })
        }
    }
}


fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}

fun ManagedActivityResultLauncher<String, Boolean>.requestCameraPermission(
    context: Context,
    onPermissionGrantedAlready: () -> Unit
) {
    val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
        // Open camera because permission is already granted
        onPermissionGrantedAlready()
    } else {
        // Request a permission
        launch(Manifest.permission.CAMERA)
    }
}

private fun ManagedActivityResultLauncher<Uri, Boolean>.requestPhotoCapture(
    context: Context,
    onFileCreated: (Uri) -> Unit
) {
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.fileprovider", file)
    onFileCreated(uri)
    launch(uri)
}

