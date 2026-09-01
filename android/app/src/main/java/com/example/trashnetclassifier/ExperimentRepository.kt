package com.example.trashnetclassifier

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class ExperimentRepository() {
    suspend fun uploadExperiment(bitmap: Bitmap): Result<List<ModelResult>> = withContext(Dispatchers.IO) {
        delay(500.milliseconds)
        val response = getMockResponse()
        Result.success(response.response)
        // TODO upload real image (bitmap)
        // TODO save image and predictions to internal storage
    }
}

private fun getMockResponse(): PredictResponse {
    val results1 = mapOf(
        "Glass" to 0.55,
        "Paper" to 0.18,
        "Plastic" to 0.12,
        "Cardboard" to 0.10,
        "Trash" to 0.03,
        "Metal" to 0.02,
    )
    val modelResult1 = ModelResult("ResNet18", results1)

    val results2 = mapOf(
        "Glass" to 0.05,
        "Paper" to 0.12,
        "Plastic" to 0.02,
        "Cardboard" to 0.16,
        "Trash" to 0.43,
        "Metal" to 0.22,
    )
    val modelResult2 = ModelResult("Basic", results2)
    val modelResults = listOf(modelResult1, modelResult2)
    return PredictResponse(modelResults)
}
