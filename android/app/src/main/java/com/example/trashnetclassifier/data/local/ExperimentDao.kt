package com.example.trashnetclassifier.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExperimentDao {
    @Query("SELECT * FROM experiments ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Experiment>>

    @Insert
    suspend fun insert(experiment: Experiment)

    @Update
    suspend fun update(experiment: Experiment)
}
