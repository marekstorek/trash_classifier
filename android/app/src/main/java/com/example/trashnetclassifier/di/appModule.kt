package com.example.trashnetclassifier.di

import com.example.trashnetclassifier.data.repository.ExperimentRepository
import com.example.trashnetclassifier.presentation.capture.CaptureViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ExperimentRepository> {
        ExperimentRepository()
    }
    viewModel {
        CaptureViewModel(get())
    }
}
