package com.svetikov.mymood.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svetikov.mymood.data.dao.ActionDao
import com.svetikov.mymood.data.model.ActionLog
import com.svetikov.mymood.data.model.EmojiFace
import com.svetikov.mymood.data.model.MoodSegment
import com.svetikov.mymood.notification.ActionDaoEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ActionViewModel @Inject constructor(
    private val dao: ActionDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val entryPoint = EntryPointAccessors.fromApplication(
        context.applicationContext,
        ActionDaoEntryPoint::class.java
    )

    val actionDao = entryPoint.actionDao()

    private val _listMoodSegment = MutableStateFlow<List<MoodSegment>>(emptyList())
    val listMoodSegment = _listMoodSegment.asStateFlow()

    private val _emojiWin = MutableStateFlow<String>("\uD83D\uDE00")
    val emojiWin = _emojiWin.asStateFlow()
    private val startDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    private val _dateGet = MutableStateFlow<String>(startDate)
    val dateGet = _dateGet.asStateFlow()

    val listWorker = actionDao.getAllActionLog().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(500),
        initialValue = emptyList()
    )

    init {

        viewModelScope.launch(Dispatchers.IO) {
            takeListMoodSelectAndEmojiWin()
        }

    }

    private suspend fun takeListMoodSelectAndEmojiWin() {
        listWorker.collectLatest { list ->
            Log.i("list collectLatest", "$list")
            if (list.isNotEmpty()) {
                _listMoodSegment.value =
                    calculatePercentEmoji(list, targetDate = _dateGet.value)
                Log.i("_listMoodSegment.value", "${_listMoodSegment.value}")
                _emojiWin.value = _listMoodSegment.value.reversed().map { it.label }.firstOrNull()
                    ?: "\uD83D\uDE00"
            }
        }
    }

    fun deleteActionLog(actionLog: ActionLog) {
        viewModelScope.launch {
            dao.deleteActionLog(actionLog)
        }
    }


    fun deleteLogs() {
        viewModelScope.launch {
            dao.deleteAllActionLog()
        }
    }


    private fun calculatePercentEmoji(
        list: List<ActionLog>,
        targetDate: String = "2025-11-14"
    ): List<MoodSegment> {
        var targetDateLocal = targetDate
        var listSortedByTimeLocal = emptyList<Pair<String, Int>>()

           val listSortByTime = list.filter {
                sortedTime(timestamp = it.timestamp, targetDateStr = targetDate)
            }
                .groupBy { it.actionType }
                .map {
                    Pair(it.key, it.value.count())
                }


        if (listSortByTime.isNotEmpty()) {
            val sumEmoji = listSortByTime
                .map { it.second.toInt() }
                .reduce { acc, i -> acc + i }

            return listSortByTime.map {
                Pair(it.first, it.second * (100.0 / sumEmoji).toFloat())
            }
                .map {
                    val color = when (it.first) {
                        EmojiFace.HAPPY.face -> Color(0xFFAEFA06)             //"😀"
                        EmojiFace.CRAY.face -> Color(0xff90CAF9)              //"😢"
                        EmojiFace.ANGRY.face -> Color(0xFFEF2A2A)             //"😡"
                        EmojiFace.SLEEP.face -> Color(0xFF534AF1)             //😴
                        EmojiFace.WOW.face -> Color(0xFFFFF59D)               //😵
                        EmojiFace.LOVE.face -> Color(0xFFEF5E8F)              //🥰
                        EmojiFace.INDIFFERENCE.face -> Color(0xFF0B9CEC)      //😐
                        EmojiFace.UNAMUSED.face -> Color(0xFFB39DDB)          //😒
                        else -> Color.DarkGray
                    }
                    MoodSegment((it.second / 100.0).toFloat(), color, it.first)
                }.sortedBy { it.percentage }
        } else
            return listOf(MoodSegment(1.0f, Color.DarkGray, "empty"))

    }

    private fun sortedTime(
        timestamp: Long,
        targetDateStr: String = "2025-11-17",
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Boolean {
        val instant = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId)
        val targetDate = LocalDate.parse(targetDateStr)
        return instant.toLocalDate() == targetDate

    }

    fun getDate(format: String?) {
        _dateGet.value = format ?: "2025-11-17"
        Log.d("_time", "${_dateGet.value}")
        viewModelScope.launch(Dispatchers.IO) {
            takeListMoodSelectAndEmojiWin()
        }

    }

    fun descriptionEmoji(label: String): String {
        return when (label) {
            EmojiFace.HAPPY.face -> EmojiFace.HAPPY.name          //"😀"
            EmojiFace.CRAY.face -> EmojiFace.CRAY.name             //"😢"
            EmojiFace.ANGRY.face -> EmojiFace.ANGRY.name              //"😡"
            EmojiFace.SLEEP.face -> EmojiFace.SLEEP.name          //😴
            EmojiFace.WOW.face -> EmojiFace.WOW.name                //😵
            EmojiFace.LOVE.face -> EmojiFace.LOVE.name              //🥰
            EmojiFace.INDIFFERENCE.face -> EmojiFace.INDIFFERENCE.name    //😐
            EmojiFace.UNAMUSED.face -> EmojiFace.UNAMUSED.name  //😒
            else -> "none"
        }
    }


}

