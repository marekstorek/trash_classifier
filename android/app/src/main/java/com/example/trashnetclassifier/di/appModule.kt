package com.example.trashnetclassifier.di

import com.example.trashnetclassifier.ExperimentRepository
import org.koin.dsl.module

val appModule = module {
    single<ExperimentRepository> {
        ExperimentRepository()
    }
}
