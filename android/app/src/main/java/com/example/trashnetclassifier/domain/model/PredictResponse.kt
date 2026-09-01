package com.example.trashnetclassifier.domain.model

import com.google.gson.annotations.SerializedName

data class PredictResponse (
    @SerializedName("response") var response: List<ModelResult>
) {
    val mostTrustedModelResult: ModelResult
        get() {
            return response.maxBy { it.bestResult.confidence }
        }
}
