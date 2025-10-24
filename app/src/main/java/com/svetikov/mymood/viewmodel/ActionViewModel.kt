package com.svetikov.mymood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svetikov.mymood.data.dao.ActionDao
import com.svetikov.mymood.data.model.ActionLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActionViewModel @Inject constructor(private val dao: ActionDao) :ViewModel(){
    val logs:StateFlow<List<ActionLog>> = dao.getAllActionLog()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createLogs(){
        viewModelScope.launch {
            dao.deleteAllActionLog()
        }
    }
}