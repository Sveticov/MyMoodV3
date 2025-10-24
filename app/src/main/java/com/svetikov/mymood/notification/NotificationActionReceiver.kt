package com.svetikov.mymood.notification

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.impl.utils.ForceStopRunnable.BroadcastReceiver
import com.svetikov.mymood.data.dao.ActionDao
import com.svetikov.mymood.data.model.ActionLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {
    lateinit var actionDao: ActionDao
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        Log.d("Receiver","Action receiver $action")

        val actionType = when(action){
            "ACTION_A_CLICKED" -> "Button A"
            "ACTION_B_CLICKED" -> "Button B"
            else -> "Unknow way"
        }

        CoroutineScope(Dispatchers.IO).launch {
            actionDao.insertAction(ActionLog(actionType=actionType))
            Log.d("Receiver","Log saved: $actionType")
        }
    }

}