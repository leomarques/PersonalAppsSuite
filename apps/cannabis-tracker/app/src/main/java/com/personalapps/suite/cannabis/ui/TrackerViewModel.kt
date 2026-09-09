package com.personalapps.suite.cannabis.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.personalapps.suite.cannabis.data.AppDatabase
import com.personalapps.suite.cannabis.data.DailyCount
import com.personalapps.suite.cannabis.data.DayPreferences
import com.personalapps.suite.cannabis.data.ProjectionPreferences
import com.personalapps.suite.cannabis.data.UsageDao
import com.personalapps.suite.cannabis.data.UsageEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: UsageDao = AppDatabase.getDatabase(application).usageDao()
    private val dayPreferences = DayPreferences(application)
    private val projectionPreferences = ProjectionPreferences(application)
    private val workManager = WorkManager.getInstance(application)

    init {
        workManager.cancelUniqueWork(REMINDER_WORK_NAME)
    }

    val projectionIntervalMs: StateFlow<Long> = projectionPreferences.intervalMinutesFlow
        .map { it * 60L * 1000L }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProjectionPreferences.DEFAULT_INTERVAL_MINUTES * 60L * 1000L
        )

    val activeDayStart: StateFlow<Long> = dayPreferences.activeDayStartFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0L
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val todayUsages: StateFlow<List<UsageEntity>> = activeDayStart.flatMapLatest { dayStart ->
        if (dayStart <= 0L) flowOf(emptyList())
        else dao.getForDayStart(dayStart)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val dailyCounts: StateFlow<List<DailyCount>> = combine(dao.getDailyCounts(), activeDayStart) { counts, currentDayStart ->
        counts.filter { it.dayStart != currentDayStart }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val todayCount: StateFlow<Int> = todayUsages.map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val usageStats: StateFlow<UsageStats> = combine(todayUsages, projectionIntervalMs) { usages, intervalMs ->
        val avg = if (usages.size < 2) null else {
            var sum = 0L
            for (i in 1 until usages.size) {
                sum += usages[i].timestamp - usages[i - 1].timestamp
            }
            sum / (usages.size - 1)
        }
        val last = usages.lastOrNull()?.timestamp
        UsageStats(
            averageGapMs = avg,
            lastTimestamp = last,
            projectedNextMs = last?.plus(intervalMs)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UsageStats(null, null, null)
    )

    val dailyAverage: StateFlow<Float> = dailyCounts.map { counts ->
        val completedDays = counts.take(LAST_COMPLETED_DAYS_FOR_AVERAGE)
        if (completedDays.isEmpty()) 0f
        else completedDays.sumOf { it.count }.toFloat() / completedDays.size
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0f
    )

    fun logUsage() {
        viewModelScope.launch {
            val dayStart = activeDayStart.value
            dao.insert(UsageEntity(timestamp = System.currentTimeMillis(), dayStart = dayStart))
        }
    }

    fun deleteUsage(usage: UsageEntity) {
        viewModelScope.launch {
            dao.delete(usage)
        }
    }

    fun updateUsageTimestamp(id: Long, timestamp: Long) {
        viewModelScope.launch {
            dao.updateTimestamp(id, timestamp)
        }
    }

    fun deleteDay(dayStart: Long) {
        viewModelScope.launch {
            dao.deleteByDayStart(dayStart)
        }
    }

    fun startNewDay() {
        viewModelScope.launch {
            dayPreferences.startNewDay()
        }
    }
}

data class UsageStats(
    val averageGapMs: Long?,
    val lastTimestamp: Long?,
    val projectedNextMs: Long?
)

private const val REMINDER_WORK_NAME = "usage_reminder_work"
private const val LAST_COMPLETED_DAYS_FOR_AVERAGE = 7
