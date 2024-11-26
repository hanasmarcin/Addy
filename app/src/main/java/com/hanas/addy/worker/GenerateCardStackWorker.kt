package com.hanas.addy.worker

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class GenerateCardStackWorker(
    private val appContext: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val functions by lazy { Firebase.functions("europe-north1") }
    private val auth by lazy { Firebase.auth }
    private val storage by lazy { Firebase.storage }
    private val cancellationToken = CancellationTokenSource()

    private val errorHandler = CoroutineExceptionHandler { context, throwable ->
        Log.e("GenerateCardStackWorker", "Error", throwable)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO + errorHandler) {
            try {
                val images = workerParams.inputData.getStringArray("imagesUri")?.map { it.toUri() }

                // Do the work here--in this case, upload the images.
                val userId = auth.currentUser?.uid
                val cardStackId = UUID.randomUUID().toString()
                val metadata = StorageMetadata.Builder()
                    .setCustomMetadata("User ID", userId)
                    .setCustomMetadata("Card stack ID", cardStackId)
                    .build()
                val tasks = images?.map {
                    storage.reference.child("images/$cardStackId/${it.lastPathSegment}").putFile(it, metadata)
                }
                tasks?.onEach { it.await() }
                val result = functions.getHttpsCallable("generate_card_stack")
                    .call(mapOf("cardStackId" to cardStackId))
                    .await(cancellationToken)
                result.data
                // Indicate whether the work finished successfully with the Result
                Result.success()
            } catch (e: Throwable) {
                Result.failure()
            }
        }
    }
}
