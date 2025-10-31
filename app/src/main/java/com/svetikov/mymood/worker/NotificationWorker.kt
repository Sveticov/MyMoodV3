package com.svetikov.mymood.worker

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.svetikov.mymood.notification.NotificationActionReceiver
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.random.Random

class NotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("Worker", "Executing periodic notification task.")
        showNotification()
        return Result.success()
    }

    @SuppressLint("ResourceType")
    private fun showNotification() {
        val notificationId = Random.nextInt()
        val channelId = "TWO_HOUR_CHANNEL"
        //Pending Button A
        val intentA = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
            action = "ACTION_A_CLICKED"
        }
        val pendingIntentA = PendingIntent.getBroadcast(
            applicationContext,
            notificationId + 1,
            intentA,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        //Pending Button B
        val intentB = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
            action = "ACTION_B_CLICKED"
        }
        val pendingIntentB = PendingIntent.getBroadcast(
            applicationContext,
            notificationId + 2,
            intentB,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        //Pending Button C
        val intentC = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
            action = "ACTION_C_CLICKED"
           /* putExtra("emoji","😊")*/
        }
        val pendingIntentC = PendingIntent.getBroadcast(
            applicationContext,
            notificationId + 3,
            intentC,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )




        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_myplaces) //todo need to change
            .setContentTitle("Your two hours message")
            .setContentText("Please check your way")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

            .addAction(android.R.drawable.btn_star_big_on, "😀", pendingIntentA)
            .addAction(android.R.drawable.btn_star_big_on, "😢", pendingIntentB)
            .addAction(android.R.drawable.btn_star_big_on, "😡", pendingIntentC)

            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {

            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId, builder.build())
        }

    }
}



