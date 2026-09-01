package com.example.trashnetclassifier

import com.google.gson.annotations.SerializedName

data class ModelResult (
    @SerializedName("model_name") val modelName: String,
    @SerializedName("result") private val resultsMap: Map<String, Double>
) {
    val results: List<ClassPrediction>
        get() {
            return resultsMap
                .toClassPrediction()
                .sortedByDescending { it.confidence }
        }
    val bestResult: ClassPrediction
        get() {
            return results.first()
        }
}

private fun Map<String, Double>.toClassPrediction() : List<ClassPrediction> {
    return this.map { ClassPrediction(it.key, it.value) }
}
