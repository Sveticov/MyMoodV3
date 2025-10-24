package com.svetikov.mymood.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.svetikov.mymood.viewmodel.ActionViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun ActionLogScreen(modifier: Modifier = Modifier, viewModel: ActionViewModel = hiltViewModel()) {
    val logs by viewModel.logs.collectAsState()
    val dateFormatter = SimpleDateFormat("HH:mm:ss dd/MM/yy", Locale.getDefault())

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
        items(logs){log->
            Column {
                Text(log.actionType)
                Text("Time: ${dateFormatter.format(log.timestamp)}")
            }

        }
    }
}