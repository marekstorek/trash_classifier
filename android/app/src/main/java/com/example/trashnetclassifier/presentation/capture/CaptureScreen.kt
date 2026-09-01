package com.example.trashnetclassifier.presentation.capture

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun CaptureScreen(
    onBack: () -> Unit,
    viewModel: CaptureViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopBar(onClick = onBack)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is CaptureUiState.LiveCamera -> {
                    CameraView(
                        onPhotoCaptured = { bitmap ->
                            viewModel.onPhotoCaptured(bitmap)
                        },
                    )
                }
                is CaptureUiState.Preview -> {
                    CapturedImagePreview(
                        bitmap = state.bitmap,
                        isUploading = state.isUploading,
                        onRetake = { viewModel.resetToCamera() },
                        onSend = { viewModel.uploadImage(state.bitmap) },
                    )
                }
                is CaptureUiState.ClassificationResult -> {
                    PredictionResultView(
                        state = state,
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onClick: () -> Unit) {
    TopAppBar(
        title = {
            Text("Capture Trash")
        },
        navigationIcon = {
            IconButton(onClick) {
                Icon(Icons.Default.ArrowBackIosNew, "Back")
            }
        }
    )
}
