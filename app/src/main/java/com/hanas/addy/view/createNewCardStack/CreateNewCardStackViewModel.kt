package com.hanas.addy.view.createNewCardStack

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import com.hanas.addy.model.Answer
import com.hanas.addy.model.Attribute
import com.hanas.addy.model.Attributes
import com.hanas.addy.model.DataHolder
import com.hanas.addy.model.PlayCardData
import com.hanas.addy.model.PlayCardStack
import com.hanas.addy.model.PlayCardStackDTO
import com.hanas.addy.model.Question
import com.hanas.addy.view.gameSession.chooseGameSession.NavigationRequester
import com.hanas.addy.worker.GenerateCardStackWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class CreateNewCardStackViewModel(
    private val workManager: WorkManager,
) : ViewModel(), NavigationRequester by NavigationRequester() {
    private val _cardStackFlow = MutableStateFlow<DataHolder<Unit>>(DataHolder.Idle())
    val cardStackFlow: StateFlow<DataHolder<Unit>>
        get() = _cardStackFlow

    private val _photoUrisFlow = MutableStateFlow<List<Uri>>(emptyList())
    val photoUrisFlow: StateFlow<List<Uri>>
        get() = _photoUrisFlow

    fun addPhoto(uri: Uri) {
        _photoUrisFlow.value = _photoUrisFlow.value.toMutableList().apply { add(uri) }
    }

    fun addAllPhotos(uris: Collection<Uri>) {
        _photoUrisFlow.value = _photoUrisFlow.value.toMutableList().apply { addAll(uris) }
    }

    fun deleteGeneratedStack() {
        _cardStackFlow.value = DataHolder.Idle()
    }

    fun generateStack() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<GenerateCardStackWorker>()
            .setInputData(
                Data.Builder()
                    .putStringArray(
                        /* key = */ "imagesUri",
                        /* value = */ _photoUrisFlow.value.map { it.toString() }.toTypedArray()
                    )
                    .build()
            )
            .setConstraints(constraints)
            .addTag("GenerateCardStackWorker")
            .build()

        viewModelScope.launch {
            workManager
                .enqueue(request)
                .await()
            workManager.getWorkInfoByIdLiveData(request.id).asFlow()
                .onEach { workInfo ->
                    when (workInfo?.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            Log.d("CreateNewCardStackViewModel", "Work succeeded")
                            workInfo.outputData
                            _cardStackFlow.value = DataHolder.Success(Unit)
                        }

                        WorkInfo.State.FAILED -> {
                            Log.e("CreateNewCardStackViewModel", "Work failed")
                            _cardStackFlow.value =
                                DataHolder.Error(Throwable("Failed to generate stack"))
                        }

                        WorkInfo.State.RUNNING -> {
                            Log.d("CreateNewCardStackViewModel", "Work running")
                            _cardStackFlow.value = DataHolder.Loading()
                        }

                        else -> {
                            // Handle other states like ENQUEUED, CANCELLED, etc., if necessary
                            Log.d("CreateNewCardStackViewModel", "Work state: ${workInfo?.state}")
                        }
                    }
                }
                .launchIn(viewModelScope) // Launch the flow collection in the ViewModel's scope

        }
    }
}

fun PlayCardStackDTO.mapToPlayCardStack(id: String) = PlayCardStack(
    id = id,
    title = title,
    createdBy = createdBy,
    creationTimestamp = creationTimestamp.toDate(),
    cards = cards.map {
        PlayCardData(
            id = it.id,
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
            imagePrompt = it.imagePrompt,
            imagePath = it.imagePath,
            attributes = Attributes(
                green = Attribute(greenName, it.greenValue),
                red = Attribute(redName, it.redValue),
                blue = Attribute(blueName, it.blueValue)
            )
        )
    }
)