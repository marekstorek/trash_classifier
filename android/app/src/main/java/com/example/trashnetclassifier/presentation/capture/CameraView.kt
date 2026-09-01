package com.example.trashnetclassifier.presentation.capture

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun CameraView(
    onPhotoCaptured: (Bitmap) -> Unit,
    onOpenGallery: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        // 1:1 Square Visual Guide
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(1f)
                .align(Alignment.Center)
                .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onOpenGallery) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color.White)
            }

            // Capture Shutter
            Button(
                onClick = {
                    val executor = ContextCompat.getMainExecutor(context)
                    imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(imageProxy: ImageProxy) {
                            val bitmap = imageProxy.toBitmap()
                            val cropped = cropToCenterSquare(bitmap, imageProxy.imageInfo.rotationDegrees)
                            val resized = cropped.scale(256, 256)
                            imageProxy.close()
                            onPhotoCaptured(resized)
                        }
                        override fun onError(exception: ImageCaptureException) {
                            // imageProxyCloseFallback(exception)
                        }
                    })
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.size(72.dp)
            ) {}

            Spacer(modifier = Modifier.size(48.dp))

        }
    }
}

fun cropToCenterSquare(srcBmp: Bitmap, rotationDegrees: Int): Bitmap {
    val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
    val rotated = Bitmap.createBitmap(srcBmp, 0, 0, srcBmp.width, srcBmp.height, matrix, true)

    val edge = minOf(rotated.width, rotated.height)
    val xOffset = (rotated.width - edge) / 2
    val yOffset = (rotated.height - edge) / 2

    return Bitmap.createBitmap(rotated, xOffset, yOffset, edge, edge)
}
