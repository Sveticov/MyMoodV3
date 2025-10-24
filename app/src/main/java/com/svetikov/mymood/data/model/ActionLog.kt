package com.svetikov.mymood.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "action_log")
data class ActionLog(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val actionType:String,//Button A and B
    val timestamp:Long = System.currentTimeMillis()
)
