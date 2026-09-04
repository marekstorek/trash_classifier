package com.example.trashnetclassifier.di

import com.example.trashnetclassifier.data.local.AppDatabase
import com.example.trashnetclassifier.data.remote.ApiService
import com.example.trashnetclassifier.data.repository.ExperimentRepository
import com.example.trashnetclassifier.data.repository.HistoryRepository
import com.example.trashnetclassifier.presentation.capture.CaptureViewModel
import com.example.trashnetclassifier.presentation.history.HistoryViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://trashnet-api-856148912863.europe-west1.run.app/"

val appModule = module {
    single<ExperimentRepository> {
        ExperimentRepository(androidContext(), get(), get())
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

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
