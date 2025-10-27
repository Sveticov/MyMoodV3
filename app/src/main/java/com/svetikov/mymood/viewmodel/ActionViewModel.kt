package com.svetikov.mymood.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svetikov.mymood.data.dao.ActionDao
import com.svetikov.mymood.data.model.ActionLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActionViewModel @Inject constructor(private val dao: ActionDao) : ViewModel() {
    val logs: StateFlow<List<ActionLog>> = dao.getAllActionLog()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {

        viewModelScope.launch(Dispatchers.IO) {
            Log.i("logs","viewModel init()")
            Log.d("logs", "logs viewModel ${dao.getAllActionLog().single()}")
        }
    }

    val _logs = MutableStateFlow<List<ActionLog>>(mutableListOf())
    val myLogs = _logs.asStateFlow()

    fun getMyLogs(): List<ActionLog> {
        var list: List<ActionLog> = listOf(ActionLog(actionType = "some"))
        viewModelScope.launch {
            list = dao.getAllActionLog().single()
        }
        return list
    }

    fun createLogs() {
        viewModelScope.launch {
            dao.deleteAllActionLog()
        }
    }
}