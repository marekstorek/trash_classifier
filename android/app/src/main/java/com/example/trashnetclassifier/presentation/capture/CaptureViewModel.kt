package com.example.trashnetclassifier.presentation.capture

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashnetclassifier.ExperimentRepository
import com.example.trashnetclassifier.ModelResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CaptureUiState {
    data object LiveCamera : CaptureUiState
    data class Preview(
        val bitmap: Bitmap,
        val isUploading: Boolean = false,
        val errorMessage: String? = null,
    ) : CaptureUiState
    data class ClassificationResult (
        val bitmap: Bitmap,
        val modelResults: List<ModelResult>,
    ) : CaptureUiState
}

class CaptureViewModel(
    private val repository: ExperimentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CaptureUiState>(CaptureUiState.LiveCamera)
    val uiState: StateFlow<CaptureUiState> = _uiState.asStateFlow()

    fun onPhotoCaptured(bitmap: Bitmap) {
        _uiState.value = CaptureUiState.Preview(bitmap = bitmap)
    }

    fun resetToCamera() {
        _uiState.value = CaptureUiState.LiveCamera
    }

    fun uploadImage(bitmap: Bitmap) {
        val currentState = _uiState.value
        if (currentState !is CaptureUiState.Preview) return

        _uiState.value = currentState.copy(isUploading = true, errorMessage = null)

        viewModelScope.launch {
            val result = repository.uploadExperiment(bitmap)
            result.onSuccess {
                _uiState.value = CaptureUiState.ClassificationResult(bitmap, result.getOrNull()!!)
            }.onFailure { error ->
                _uiState.value = currentState.copy(
                    isUploading = false,
                    errorMessage = error.localizedMessage ?: "Failed to upload image"
                )
            }
        }
    }
}
