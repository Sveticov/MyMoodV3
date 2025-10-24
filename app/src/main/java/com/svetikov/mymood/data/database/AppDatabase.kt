package com.svetikov.mymood.data.database

import androidx.room.Database
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
}