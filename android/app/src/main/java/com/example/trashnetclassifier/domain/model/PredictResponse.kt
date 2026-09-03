package com.example.trashnetclassifier.domain.model

import com.google.gson.annotations.SerializedName

data class PredictResponse (
    @SerializedName("response") var response: List<ModelResult>
)

fun List<ModelResult>.mostTrustedModelResult(): ModelResult{
    return this.maxBy { it.bestResult.confidence }
}
