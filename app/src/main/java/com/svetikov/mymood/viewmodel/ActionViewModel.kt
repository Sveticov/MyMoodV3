package com.svetikov.mymood.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svetikov.mymood.data.dao.ActionDao
import com.svetikov.mymood.data.model.ActionLog
import com.svetikov.mymood.notification.ActionDaoEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActionViewModel @Inject constructor(
    private val dao: ActionDao,
    @ApplicationContext private val context: Context
) : ViewModel() {
    val logs: StateFlow<List<ActionLog>> = dao.getAllActionLog()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {

        viewModelScope.launch(Dispatchers.IO) {
            Log.i("logs", "viewModel init()")
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                ActionDaoEntryPoint::class.java
            )
            val actionDao = entryPoint.actionDao()
            Log.d("log", "log viewModel: ${actionDao.getAllActionLog().first()}")
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