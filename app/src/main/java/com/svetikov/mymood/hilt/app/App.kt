package com.svetikov.mymood.hilt.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import java.time.LocalDateTime

@HiltAndroidApp
class App:Application() {
    override fun onCreate() {
        Log.d("SETTING 1","setting ${LocalDateTime.now()}")
        super.onCreate()
        createNotificationChannel()
      /*  if(isFirstLaunch(this)){
            Log.d("SETTING 2","setting ${LocalDateTime.now()}")
            if (!isNotificationEnabled(this)){
                Log.d("SETTING 3","setting ${LocalDateTime.now()}")
                openNotificationSetting(this)
            }
        }*/
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val name = "Periodic Remainder"
            val descriptionText = "Channel for two-hourly notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("TWO_HOUR_CHANNEL",name,importance).apply {
                description=descriptionText
            }
            val notificationManager:NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

/*    fun isFirstLaunch(context: Context):Boolean{
        val pref = context.getSharedPreferences("app_pref",Context.MODE_PRIVATE)
        val firstLaunch = pref.getBoolean("first_launch",true)

        if(firstLaunch){
            pref.edit().putBoolean("first_launch",false).apply()
        }
        return firstLaunch
    }

    fun isNotificationEnabled(context: Context):Boolean{
        val manager = NotificationManagerCompat.from(context)
        return manager.areNotificationsEnabled()
    }*/



    fun openNotificationSetting(context: Context){
        val intent  = Intent( Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {

            putExtra(Settings.EXTRA_APP_PACKAGE,context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

