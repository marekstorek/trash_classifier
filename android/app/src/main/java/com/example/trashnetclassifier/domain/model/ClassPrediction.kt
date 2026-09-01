package com.example.trashnetclassifier.domain.model

data class ClassPrediction(
    val className: String,
    val confidence: Double,
)
