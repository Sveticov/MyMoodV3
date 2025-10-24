package com.svetikov.mymood.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.svetikov.mymood.data.model.ActionLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionDao {
    @Insert
    suspend fun insertAction(actionLog: ActionLog)
    @Query("SELECT*FROM action_log ORDER BY timestamp DESC")
    fun getAllActionLog(): Flow<List<ActionLog>>
    @Query("DELETE FROM action_log")
    suspend fun deleteAllActionLog()
}