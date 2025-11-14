package com.svetikov.mymood.notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.work.impl.utils.ForceStopRunnable.BroadcastReceiver
import com.svetikov.mymood.data.dao.ActionDao
import com.svetikov.mymood.data.model.ActionLog
import com.svetikov.mymood.worker.ID_NOTIFICATION
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@EntryPoint
@InstallIn(SingletonComponent::class)
interface ActionDaoEntryPoint {
    fun actionDao(): ActionDao
}

@SuppressLint("RestrictedApi")
//@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {
    //@Inject  lateinit var actionDao: ActionDao

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        Log.d("Receiver", "Action receiver $action")

        val actionType = when (action) {
            "ACTION_A_CLICKED" -> "😀"
            "ACTION_B_CLICKED" -> "😢"
            "ACTION_C_CLICKED" -> "😡"
            "ACTION_D_CLICKED" -> "\uD83D\uDE34"//😴
            "ACTION_E_CLICKED" -> "\uD83D\uDE35"//😵
            "ACTION_F_CLICKED" -> "\uD83E\uDD70"//🥰
            "ACTION_G_CLICKED" -> "\uD83D\uDE10"//😐
            "ACTION_V_CLICKED" -> "\uD83D\uDE12"//😒
            else -> "Unknow way"
        }

        val notificationManager = NotificationManagerCompat.from(context!!)
        when (action) {
            "ACTION_A_CLICKED" -> {
                notificationManager.cancel(ID_NOTIFICATION )
            }

            "ACTION_B_CLICKED"

                -> {
                notificationManager.cancel(ID_NOTIFICATION )
            }

            "ACTION_C_CLICKED"

                -> {
                notificationManager.cancel(ID_NOTIFICATION )
            }

            "ACTION_D_CLICKED"

                -> {
                notificationManager.cancel(ID_NOTIFICATION )
            }

            "ACTION_E_CLICKED"

                -> {
                notificationManager.cancel(ID_NOTIFICATION )
            }

            "ACTION_F_CLICKED"

                -> {
                notificationManager.cancel(ID_NOTIFICATION )
            }

            "ACTION_G_CLICKED"

                -> {
                notificationManager.cancel(ID_NOTIFICATION )
            }

            "ACTION_V_CLICKED"

                -> {
                notificationManager.cancel(ID_NOTIFICATION )
            }

            else -> {
                notificationManager.cancel(ID_NOTIFICATION )
            }
        }



        CoroutineScope(Dispatchers.IO).launch {
            try {
                val hiltEntryPoint = EntryPointAccessors.fromApplication(
                    context = context.applicationContext,
                    ActionDaoEntryPoint::class.java
                )

                val actionDao = hiltEntryPoint.actionDao()
                actionDao.insertAction(ActionLog(actionType = actionType))
                Log.d("Receiver", "Log saved: $actionType")

            } catch (e: Exception) {
                Log.e("Receiver", "Filed to insert action ", e)
            }

        }
    }

}