package com.svetikov.mymood.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.svetikov.mymood.viewmodel.ActionViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun ActionLogScreen(modifier: Modifier = Modifier, viewModel: ActionViewModel = hiltViewModel()) {

    val actionLogList by viewModel.listWorker.collectAsState()


    val dateFormatter = SimpleDateFormat("HH:mm:ss dd/MM/yy", Locale.getDefault())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBE7BB))
            .padding(12.dp)
    ) {
        Text("Hello")
        LazyColumn(
            // modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(actionLogList) { actionLog ->
                Card(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    ),
                    modifier = Modifier.padding(16.dp)
                        .fillMaxWidth(),
                    onClick = {viewModel.deleteActionLog(actionLog)},
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF9AE79A)
                    )
                ) {
                    Column (modifier=Modifier.padding(4.dp)){
                        Text(actionLog.actionType)
                        Text("Time: ${dateFormatter.format(actionLog.timestamp)}")
                    }
                }

            }
        }

        Spacer(modifier = Modifier.padding(top = 100.dp))

    }
}