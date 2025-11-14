package com.svetikov.mymood.worker

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.svetikov.mymood.R
import com.svetikov.mymood.notification.NotificationActionReceiver
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlin.random.Random
var ID_NOTIFICATION = 0
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
        ID_NOTIFICATION = notificationId
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
        //Pending Button D
        val intentD = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
            action = "ACTION_D_CLICKED"
            /* putExtra("emoji","😴")*/
        }
        val pendingIntentD = PendingIntent.getBroadcast(
            applicationContext,
            notificationId + 4,
            intentD,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        //Pending Button E
        val intentE = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
            action = "ACTION_E_CLICKED"
            /* putExtra("emoji","😵")*/
        }
        val pendingIntentE = PendingIntent.getBroadcast(
            applicationContext,
            notificationId + 5,
            intentE,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        //Pending Button F
        val intentF = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
            action = "ACTION_F_CLICKED"
            /* putExtra("emoji","😊")*/
        }
        val pendingIntentF = PendingIntent.getBroadcast(
            applicationContext,
            notificationId + 6,
            intentF,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        //Pending Button G
        val intentG = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
            action = "ACTION_G_CLICKED"
            /* putExtra("emoji","😊")*/
        }
        val pendingIntentG = PendingIntent.getBroadcast(
            applicationContext,
            notificationId + 7,
            intentG,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        //Pending Button V
        val intentV = Intent(applicationContext, NotificationActionReceiver::class.java).apply {
            action = "ACTION_V_CLICKED"
            /* putExtra("emoji","😊")*/
        }
        val pendingIntentV = PendingIntent.getBroadcast(
            applicationContext,
            notificationId + 8,
            intentV,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val customView =
            RemoteViews(applicationContext.packageName, R.layout.notification_custom_layout)
        customView.setOnClickPendingIntent(R.id.action_button_1, pendingIntentA)
        customView.setOnClickPendingIntent(R.id.action_button_2, pendingIntentB)
        customView.setOnClickPendingIntent(R.id.action_button_3, pendingIntentC)
        customView.setOnClickPendingIntent(R.id.action_button_4, pendingIntentD)
        customView.setOnClickPendingIntent(R.id.action_button_5, pendingIntentE)
        customView.setOnClickPendingIntent(R.id.action_button_6, pendingIntentF)
        customView.setOnClickPendingIntent(R.id.action_button_7, pendingIntentG)
        customView.setOnClickPendingIntent(R.id.action_button_8, pendingIntentV)

        customView.setTextViewText(R.id.notification_title, "Your two hours message")
        customView.setTextViewText(R.id.notification_text, "Please check your way")


        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_myplaces) //todo need to change
            /* .setContentTitle("Your two hours message")
             .setContentText("Please check your way")*/
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            //
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(customView)
            //
            /* .addAction(android.R.drawable.btn_star_big_on, "😀", pendingIntentA)
             .addAction(android.R.drawable.btn_star_big_on, "😢", pendingIntentB)
             .addAction(android.R.drawable.btn_star_big_on, "😡", pendingIntentC)
             .addAction(android.R.drawable.btn_star_big_on, "😡", pendingIntentC)
             .addAction(android.R.drawable.btn_star_big_on, "😡", pendingIntentC)*/


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





