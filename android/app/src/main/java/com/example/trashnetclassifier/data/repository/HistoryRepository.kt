package com.example.trashnetclassifier.data.repository

import com.example.trashnetclassifier.data.local.Experiment
import com.example.trashnetclassifier.data.local.ExperimentDao
import kotlinx.coroutines.flow.Flow

class HistoryRepository(
    private val dao: ExperimentDao,
) {
    val allExperiments: Flow<List<Experiment>> = dao.getAll()
}
