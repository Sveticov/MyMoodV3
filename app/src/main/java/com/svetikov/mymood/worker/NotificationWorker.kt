package com.svetikov.mymood.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.svetikov.mymood.R
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
        //Pending Button A
        val intentB = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
            action = "ACTION_B_CLICKED"
        }
        val pendingIntentB = PendingIntent.getBroadcast(
            applicationContext,
            notificationId + 2,
            intentB,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(applicationContext,channelId)
            .setSmallIcon(R.drawable.ic_launcher_background) //todo need to change
            .setContentTitle("Your two hours message")
            .setContentText("Please check your way")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(android.R.drawable.btn_star_big_on,"Button A",pendingIntentA)
            .addAction(android.R.drawable.btn_star_big_on,"Button B",pendingIntentB)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)){
            notify(notificationId,builder.build())
        }

    }
}