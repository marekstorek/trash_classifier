package com.example.trashnetclassifier.presentation.capture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashnetclassifier.data.local.Experiment
import com.example.trashnetclassifier.data.repository.ExperimentRepository
import com.example.trashnetclassifier.domain.model.ModelResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CaptureUiState {
    val experiment: Experiment?
    data class LiveCamera(
        override val experiment: Experiment? = null,
    ) : CaptureUiState
    data class Preview(
        val bitmap: Bitmap,
        override val experiment: Experiment?,
        val isUploading: Boolean = false,
        val errorMessage: String? = null,
    ) : CaptureUiState
    data class ClassificationResult (
        val bitmap: Bitmap,
        override val experiment: Experiment,
        val modelResults: List<ModelResult>,
    ) : CaptureUiState {
        val mostTrustedClassName: String
            get() {
                val mostTrustedModelResult = modelResults.maxBy { it.bestResult.confidence }
                return mostTrustedModelResult.bestResult.className
            }
    }
}

class CaptureViewModel(
    private val repository: ExperimentRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CaptureUiState>(CaptureUiState.LiveCamera())
    val uiState: StateFlow<CaptureUiState> = _uiState.asStateFlow()

    fun onPhotoCaptured(bitmap: Bitmap) {
        _uiState.value = CaptureUiState.Preview(bitmap = bitmap, experiment = _uiState.value.experiment)
    }

    fun onImageSelected(context: Context, uri: Uri) {
        viewModelScope.launch {
            val bitmap = loadAndCropUriToSquare(context, uri)
            bitmap?.let {
                _uiState.value = CaptureUiState.Preview(bitmap = it, experiment = _uiState.value.experiment)
            }
        }
    }

    private fun loadAndCropUriToSquare(context: Context, uri: Uri): Bitmap? {
        val original = try {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri)) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
        } catch (e: Exception) {
            return null
        }

        val edge = minOf(original.width, original.height)
        val xOffset = (original.width - edge) / 2
        val yOffset = (original.height - edge) / 2

        return Bitmap.createBitmap(original, xOffset, yOffset, edge, edge)
    }

    fun resetToCamera() {
        _uiState.value = CaptureUiState.LiveCamera()
    }

    fun uploadImage(bitmap: Bitmap) {
        val currentState = _uiState.value
        if (currentState !is CaptureUiState.Preview) return

        val file = repository.saveBitmapToInternalStorage(bitmap)
        var experiment = Experiment(localImagePath = file.path)

        _uiState.value = currentState.copy(isUploading = true, errorMessage = null)
        viewModelScope.launch {
            val experimentId = repository.saveExperiment(experiment)
            experiment = experiment.copy(id = experimentId)

            val result = repository.uploadImage(bitmap)
            result.onSuccess {
                val modelResult = result.getOrNull()!!
                experiment = experiment.copy(modelResults = modelResult)
                repository.updateExperiment(experiment)
                _uiState.value = CaptureUiState.ClassificationResult(bitmap, experiment, modelResult)
            }.onFailure { error ->
                _uiState.value = currentState.copy(
                    isUploading = false,
                    errorMessage = error.localizedMessage ?: "Failed to upload image"
                )
            }
        }
    }

    fun confirmPrediction() {
        val currentState = _uiState.value
        if (currentState !is CaptureUiState.ClassificationResult) return

        val correctClass = currentState.mostTrustedClassName
        val experiment = _uiState.value.experiment?.copy(correctClass = correctClass) ?: return
        _uiState.value = currentState.copy(
            experiment = experiment
        )

        viewModelScope.launch {
            repository.updateExperiment(experiment)
        }
    }

    fun submitLabelCorrection(trueLabel: String) {
        val currentState = _uiState.value
        if (currentState !is CaptureUiState.ClassificationResult) return

        val experiment = _uiState.value.experiment?.copy(correctClass = trueLabel) ?: return
        _uiState.value = currentState.copy(
            experiment = experiment
        )

        viewModelScope.launch {
            repository.updateExperiment(experiment)
        }
    }
}
