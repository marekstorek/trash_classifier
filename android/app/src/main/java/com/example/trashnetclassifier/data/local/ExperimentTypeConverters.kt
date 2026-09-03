package com.example.trashnetclassifier.data.local

import androidx.room.TypeConverter
import com.example.trashnetclassifier.domain.model.ModelResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ExperimentTypeConverters {

    private val gson = Gson()

    @TypeConverter
    fun fromModelResultList(value: List<ModelResult>?): String? {
        if (value == null) return null
        val type = object : TypeToken<List<ModelResult>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toModelResultList(value: String?): List<ModelResult>? {
        if (value.isNullOrEmpty()) return null
        val type = object : TypeToken<List<ModelResult>>() {}.type
        return gson.fromJson(value, type)
    }
}