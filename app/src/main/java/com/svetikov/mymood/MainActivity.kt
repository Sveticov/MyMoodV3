package com.svetikov.mymood

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
        Log.d("onCreate", "onCreate")
        //schedule
        window.decorView.post {
            schedulePeriodicNotification(this)
            scheduleOne(this)
        }

        enableEdgeToEdge()
        setContent {
            val isFirstLaunch = isFirstAppLaunch()
            MyMoodTheme {

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NotificationPermissionDialog(
                        isFirstLaunch = isFirstLaunch,
                        onHandler = {markLaunchHandled()}
                    )
                    ActionLogScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }

    }



    //--------------------------work manager------------------------------
    private fun schedulePeriodicNotification(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            15,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "TwoHourNotification",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
        Log.d("workRequest", "workRequest ${workRequest.id}")
    }

    private fun scheduleOne(context: Context) {
        Log.d("Start", "START ONE Message")
        val oneTime = OneTimeWorkRequestBuilder<NotificationWorker>()
            .build()
        WorkManager.getInstance(context)
            .enqueue(oneTime)
    }

    //----------------------first launch check-------------------------
    private fun isFirstAppLaunch(): Boolean {
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val first = prefs.getBoolean("firstLaunch", true)
        /*   if (first) {
               prefs.edit().putBoolean("firstLaunch", false).apply()
           }*/
        Log.d("launch 2", "${first}")
        return first
    }

    private fun markLaunchHandled() {
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("firstLaunch", false).apply()
    }

    //----------------------permission dialog--------------------------
    @Composable
    private fun NotificationPermissionDialog(
        isFirstLaunch: Boolean,
        onHandler: () -> Unit
    ) {
        var showDialog by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val activity = context as? MainActivity

        LaunchedEffect(Unit) {
            if (isFirstLaunch) {
                kotlinx.coroutines.delay(300)
                showDialog = true
            }
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    onHandler()
                },
                title = { Text("Turn on notification") },
                text = { Text("You need switch on notification") },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        onHandler()
                        activity?.openNotificationSettings()
                    }) { Text("Settings") }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog=false
                        onHandler()
                    }) { Text("Cancel") }
                }
            )
        }

    }

    private fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        Log.d("launch 3", "${intent}")
    }
}
