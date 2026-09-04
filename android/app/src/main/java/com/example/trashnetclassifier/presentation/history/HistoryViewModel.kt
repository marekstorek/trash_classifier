package com.example.trashnetclassifier.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trashnetclassifier.data.local.Experiment
import com.example.trashnetclassifier.data.repository.HistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(
    repository: HistoryRepository,
) : ViewModel() {
    val allExperiments: StateFlow<List<Experiment>> = repository.allExperiments
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
}
