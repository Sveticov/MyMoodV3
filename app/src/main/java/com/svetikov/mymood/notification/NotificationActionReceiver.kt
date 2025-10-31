package com.svetikov.mymood.notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.impl.utils.ForceStopRunnable.BroadcastReceiver
import com.svetikov.mymood.data.dao.ActionDao
import com.svetikov.mymood.data.model.ActionLog
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@EntryPoint
@InstallIn(SingletonComponent::class)
interface ActionDaoEntryPoint{
    fun actionDao(): ActionDao
}

@SuppressLint("RestrictedApi")
//@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {
  //@Inject  lateinit var actionDao: ActionDao

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        Log.d("Receiver","Action receiver $action")

        val actionType = when(action){
            "ACTION_A_CLICKED" -> "😀"
            "ACTION_B_CLICKED" -> "😢"
            "ACTION_C_CLICKED" -> "😡"
            else -> "Unknow way"
        }



        CoroutineScope(Dispatchers.IO).launch {
            try {
                val hiltEntryPoint = EntryPointAccessors.fromApplication(
                    context = context.applicationContext,
                    ActionDaoEntryPoint::class.java
                )

                val actionDao = hiltEntryPoint.actionDao()
                actionDao.insertAction(ActionLog(actionType=actionType))
                Log.d("Receiver","Log saved: $actionType")

            }catch (e:Exception){
                Log.e("Receiver","Filed to insert action ",e)
            }

        }
    }

}