package com.svetikov.mymood.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.svetikov.mymood.data.dao.ActionDao
import com.svetikov.mymood.data.model.ActionLog
import com.svetikov.mymood.data.model.EmojiFace
import com.svetikov.mymood.data.model.MoodSegment
import com.svetikov.mymood.datastore.SettingDataStoreManager
import com.svetikov.mymood.notification.ActionDaoEntryPoint
import com.svetikov.mymood.worker.NotificationWorker
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ActionViewModel @Inject constructor(
    private val dao: ActionDao,
    @ApplicationContext private val context: Context,
    private val dataStoreManager: SettingDataStoreManager,
    /*  private val workerManager: WorkManager*/
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
    val minDate = MutableStateFlow<Long?>(null)
    val maxDate = MutableStateFlow<Long?>(null)

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

                val sortedDateMinMax = list.sortedBy { it.timestamp }
                minDate.value = sortedDateMinMax.first().timestamp
                maxDate.value = sortedDateMinMax.last().timestamp

                _listMoodSegment.value =
                    calculatePercentEmoji(list, targetDate = _dateGet.value)
                Log.i("_listMoodSegment.value", "${_listMoodSegment.value}")
                _emojiWin.value = _listMoodSegment.value.reversed().map { it.label }.firstOrNull()
                    ?: "\uD83D\uDE00"
            } else {
                minDate.value = null
                maxDate.value = null
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
                        EmojiFace.EXCITED.face -> Color(0xFFF89205)   //        //"😀"
                        EmojiFace.HAPPY.face -> Color(0xFFEEC80C)          //"😢"
                        EmojiFace.NEUTRAL.face -> Color(0xFF139BEC)           //"😡"
                        EmojiFace.TIRED.face -> Color(0xFF7986CB)           //😴
                        EmojiFace.SAD.face -> Color(0xFF4FC3F7)             //😵
                        EmojiFace.ANGRY.face -> Color(0xFFF55050)            //🥰
                        EmojiFace.ANXIOUS.face -> Color(0xFFA9E85E)    //😐
                        EmojiFace.LOVED.face -> Color(0xFFF3467C)        //😒
                        else -> Color(
                            red = Random.nextInt(200, 256),
                            green = Random.nextInt(50, 150),
                            blue = Random.nextInt(100, 180)
                        )
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
        Log.d("_time", _dateGet.value)
        viewModelScope.launch(Dispatchers.IO) {
            takeListMoodSelectAndEmojiWin()
        }

    }

    fun descriptionEmoji(label: String): String {
        return when (label) {
            EmojiFace.EXCITED.face -> EmojiFace.EXCITED.name                  //"😀"
            EmojiFace.HAPPY.face -> EmojiFace.HAPPY.name                    //"😢"
            EmojiFace.NEUTRAL.face -> EmojiFace.NEUTRAL.name                  //"😡"
            EmojiFace.TIRED.face -> EmojiFace.TIRED.name                  //😴
            EmojiFace.SAD.face -> EmojiFace.SAD.name                      //😵
            EmojiFace.ANGRY.face -> EmojiFace.ANGRY.name                    //🥰
            EmojiFace.ANXIOUS.face -> EmojiFace.ANXIOUS.name    //😐
            EmojiFace.LOVED.face -> EmojiFace.LOVED.name            //😒
            else -> EmojiFace.HAPPY.name
        }
    }

    //-----------------------------Setting Data Store Manager------------------------------
    //---read from DataStore and convert it to StateFlow-----------------------------------
    val notificationIntervalHours: StateFlow<Int> = dataStoreManager.notificationIntervalFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 1
        )

    //---save new interval-----------------------------------------------------------------
    fun updateNotificationInterval(hours: Int) {
        viewModelScope.launch {
            dataStoreManager.saveInterval(hours)
            scheduleNewPeriodicWork(hours)
            Log.d("workRequest", "workRequest ${hours}")
        }
    }
    //--------------------------schedular period work--------------------
    private fun scheduleNewPeriodicWork(intervalHours: Int) {
        val intervalMinutes = when (intervalHours) {
            1 -> 60L
            2 -> 120L
            3 -> 180L
            4 -> 240L
            5 -> 300L
            else -> 15L
        }
        val wm = WorkManager.getInstance(context)
        val workerName = "TwoHourNotification"
        wm.cancelUniqueWork(workerName)

        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            repeatInterval = intervalMinutes,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        ).build()

        wm.enqueueUniquePeriodicWork(
            uniqueWorkName = workerName,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
        Log.d("workRequest", "workRequest ${workRequest.id}")

    }

    fun initializePeriodicWork() {
        viewModelScope.launch {
            notificationIntervalHours.collect { hours ->
                scheduleNewPeriodicWork(hours)
                cancel()
            }
        }
    }

}

