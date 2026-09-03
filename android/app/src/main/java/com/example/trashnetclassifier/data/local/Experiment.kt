package com.example.trashnetclassifier.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.trashnetclassifier.domain.model.ModelResult
import com.example.trashnetclassifier.domain.model.mostTrustedModelResult

@Entity(tableName = "experiments")
data class Experiment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val localImagePath: String,
    val modelResults: List<ModelResult>? = null,
    val correctClass: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
) {
    val state: ExperimentState
        get() {
            if (modelResults == null) {
                return ExperimentState.FailedToPredict
            } else if (correctClass == null) {
                return ExperimentState.Unconfirmed
            } else if (modelResults.mostTrustedModelResult().bestResult.className == correctClass) {
                return ExperimentState.Correct
            } else if (correctClass in modelResults.map { it.bestResult.className }) {
                return ExperimentState.OneModelWasRight
            } else {
                return ExperimentState.Wrong
            }
        }

}

enum class ExperimentState {
    FailedToPredict,
    Unconfirmed,
    Correct,
    OneModelWasRight,
    Wrong,
}
