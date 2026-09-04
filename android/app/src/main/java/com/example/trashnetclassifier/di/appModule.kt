package com.example.trashnetclassifier.di

import com.example.trashnetclassifier.data.local.AppDatabase
import com.example.trashnetclassifier.data.repository.ExperimentRepository
import com.example.trashnetclassifier.data.repository.HistoryRepository
import com.example.trashnetclassifier.presentation.capture.CaptureViewModel
import com.example.trashnetclassifier.presentation.history.HistoryViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ExperimentRepository> {
        ExperimentRepository(androidContext(), get())
    }
    single<HistoryRepository> {
        HistoryRepository(get())
    }

    viewModel {
        CaptureViewModel(get())
    }
    viewModel {
        HistoryViewModel(get())
    }

    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().experimentDao() }
}
