package com.example.trashnetclassifier.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Experiment::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ExperimentTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun experimentDao(): ExperimentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trashnet_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
