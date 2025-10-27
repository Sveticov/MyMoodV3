package com.svetikov.mymood

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.svetikov.mymood.ui.pages.ActionLogScreen
import com.svetikov.mymood.ui.theme.MyMoodTheme
import com.svetikov.mymood.worker.NotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            //schedule
        schedulePeriodicNotification(this)
        scheduleOne(this)


        enableEdgeToEdge()
        setContent {
            MyMoodTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
               ActionLogScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
    private fun schedulePeriodicNotification(context: Context){
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            15,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "TwoHourNotification",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
        Log.d("workRequest","workRequest ${workRequest.id}")
    }
    private fun  scheduleOne(context: Context){
        val oneTime = OneTimeWorkRequestBuilder<NotificationWorker>()
            .build()
        WorkManager.getInstance(context)
            .enqueue(oneTime)
    }
}
