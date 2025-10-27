package com.svetikov.mymood.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.svetikov.mymood.viewmodel.ActionViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun ActionLogScreen(modifier: Modifier = Modifier, viewModel: ActionViewModel = hiltViewModel()) {
    val logs by viewModel.logs.collectAsState()


    val dateFormatter = SimpleDateFormat("HH:mm:ss dd/MM/yy", Locale.getDefault())
    Text("Hello")
    /* LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
         items(viewModel.getMyLogs()*//*logs*//*) { log ->
            Column {
                Text(log.actionType)
                Text("Time: ${dateFormatter.format(log.timestamp)}")
            }

        }


    }*/
    Column {
        Column(modifier = Modifier.fillMaxWidth()) {
            for (log in viewModel.getMyLogs()) {
                Column {
                    Text(log.actionType)
                    Text("Time: ${dateFormatter.format(log.timestamp)}")
                }

            }
        }
        Button(onClick = { viewModel.getMyLogs()
        Log.i("logs","logs button ${viewModel.getMyLogs()}")
        }) { Text("Update") }
    }
}