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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActionViewModel @Inject constructor(
    private val dao: ActionDao,
    @ApplicationContext private val context: Context
) : ViewModel() {
   /* val logs: StateFlow<List<ActionLog>> = dao.getAllActionLog()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )*/
    val entryPoint = EntryPointAccessors.fromApplication(
        context.applicationContext,
        ActionDaoEntryPoint::class.java
    )

    val actionDao = entryPoint.actionDao()

    val _listAction = MutableStateFlow<List<ActionLog>>(emptyList())
    val listAction = _listAction.asStateFlow()

    val listWorker =  actionDao.getAllActionLog().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(500),
        initialValue = emptyList()
    )

    init {
        Log.i("logs", "viewModel init()")
        Log.d("list","viewModel list: ${listWorker.value}")

        viewModelScope.launch(Dispatchers.IO) {

            Log.d("log", "log viewModel: ${actionDao.getAllActionLog().firstOrNull()}")

        }
    }

    fun deleteActionLog(actionLog: ActionLog){
        viewModelScope.launch {
            dao.deleteActionLog(actionLog)
        }
    }


    fun deleteLogs() {
        viewModelScope.launch {
            dao.deleteAllActionLog()
        }
    }
}