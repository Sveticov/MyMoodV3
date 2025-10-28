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
import kotlinx.coroutines.flow.first
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
            "ACTION_A_CLICKED" -> "Button A"
            "ACTION_B_CLICKED" -> "Button B"
            else -> "Unknow way"
        }



        CoroutineScope(Dispatchers.IO).launch {
            try {
                val hiltEntryPoint = EntryPointAccessors.fromApplication(
                    context = context.applicationContext,
                    ActionDaoEntryPoint::class.java
                )/*if(::actionDao.isInitialized){*/
                   // actionDao
              /*  }else{
                    Log.w("Receiver", "Hilt injection failed — using manual DB init")
                    val db =  AppDatabase.getInstance(context)
                    db.actionDao()

                }*/
                val actionDao = hiltEntryPoint.actionDao()
                actionDao.insertAction(ActionLog(actionType=actionType))
                Log.d("Receiver","Log saved: $actionType")
                Log.i("logs","logs: ${actionDao.getAllActionLog().first()}")
            }catch (e:Exception){
                Log.e("Receiver","Filed to insert action ",e)
            }

        }
    }

}