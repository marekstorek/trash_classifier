package com.example.trashnetclassifier.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.trashnetclassifier.data.local.Experiment
import com.example.trashnetclassifier.data.local.ExperimentDao
import com.example.trashnetclassifier.domain.model.ModelResult
import com.example.trashnetclassifier.domain.model.PredictResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

class ExperimentRepository(
    private val context: Context,
    private val dao: ExperimentDao,
) {

    suspend fun uploadImage(bitmap: Bitmap): Result<List<ModelResult>> = withContext(Dispatchers.IO) {
        delay(500.milliseconds)
        val response = getMockResponse()
        Result.success(response.response)
        // TODO upload real image (bitmap)
        // TODO save image and predictions to internal storage
    }

    fun saveBitmapToInternalStorage(bitmap: Bitmap): File {
        val fileName = "experiment_${UUID.randomUUID()}.jpg"
        val directory = File(context.filesDir, "experiments").apply {
            if (!exists()) mkdirs()
        }
        val file = File(directory, fileName)

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return file
    }

    suspend fun saveExperiment(experiment: Experiment) {
        dao.insert(experiment)
    }

    suspend fun updateExperiment(experiment: Experiment) {
        dao.update(experiment)
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
