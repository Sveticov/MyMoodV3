package com.svetikov.mymood.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.svetikov.mymood.data.dao.ActionDao
import com.svetikov.mymood.data.model.ActionLog

@Database(
    entities = [ActionLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase:RoomDatabase() {
    abstract fun actionDao():ActionDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_mood.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}