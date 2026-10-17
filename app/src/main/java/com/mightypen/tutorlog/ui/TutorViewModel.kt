package com.mightypen.tutorlog.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mightypen.tutorlog.data.TutorRepository
import com.mightypen.tutorlog.model.TutorLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class DateFilterMode(val label: String) {
    ALL("All Time"),
    UP_TO_TODAY("Up to Today"),
    THIS_MONTH("This Month"),
    THIS_WEEK("This Week"),
    TODAY("Today")
}

enum class ShareScope(val label: String) {
    UP_TO_TODAY("All Logs Up to Today"),
    ALL_TIME("All Recorded Logs"),
    FILTERED("Current Filtered View")
}

data class TutorUiState(
    val logs: List<TutorLog> = emptyList(),
    val filteredLogs: List<TutorLog> = emptyList(),
    val studentSuggestions: List<String> = emptyList(),
    val searchQuery: String = "",
    val filterMode: DateFilterMode = DateFilterMode.ALL,
    val isAddEditOpen: Boolean = false,
    val editingLog: TutorLog? = null,
    val isShareOpen: Boolean = false,
    val shareScope: ShareScope = ShareScope.UP_TO_TODAY,
    val totalMinutes: Long = 0L,
    val formattedTotalTime: String = "0h 0m",
    val uniqueStudentCount: Int = 0
)

class TutorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TutorRepository(application)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterMode = MutableStateFlow(DateFilterMode.ALL)
    val filterMode: StateFlow<DateFilterMode> = _filterMode.asStateFlow()

    private val _isAddEditOpen = MutableStateFlow(false)
    val isAddEditOpen: StateFlow<Boolean> = _isAddEditOpen.asStateFlow()

    private val _editingLog = MutableStateFlow<TutorLog?>(null)
    val editingLog: StateFlow<TutorLog?> = _editingLog.asStateFlow()

    private val _isShareOpen = MutableStateFlow(false)
    val isShareOpen: StateFlow<Boolean> = _isShareOpen.asStateFlow()

    private val _shareScope = MutableStateFlow(ShareScope.UP_TO_TODAY)
    val shareScope: StateFlow<ShareScope> = _shareScope.asStateFlow()

    val uiState: StateFlow<TutorUiState> = combine(
        repository.logsFlow,
        repository.studentNamesFlow,
        _searchQuery,
        _filterMode,
        _isAddEditOpen,
        _editingLog,
        _isShareOpen,
        _shareScope
    ) { args ->
        val logs = args[0] as List<TutorLog>
        val studentSuggestions = args[1] as List<String>
        val query = args[2] as String
        val filter = args[3] as DateFilterMode
        val addEditOpen = args[4] as Boolean
        val editing = args[5] as TutorLog?
        val shareOpen = args[6] as Boolean
        val scope = args[7] as ShareScope

        val today = LocalDate.now()
        val todayStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE)

        val filtered = logs.filter { log ->
            // Date filter
            val dateMatches = when (filter) {
                DateFilterMode.ALL -> true
                DateFilterMode.UP_TO_TODAY -> log.date <= todayStr
                DateFilterMode.TODAY -> log.date == todayStr
                DateFilterMode.THIS_WEEK -> {
                    try {
                        val parsed = LocalDate.parse(log.date)
                        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
                        !parsed.isBefore(startOfWeek) && !parsed.isAfter(today)
                    } catch (_: Exception) {
                        true
                    }
                }
                DateFilterMode.THIS_MONTH -> {
                    try {
                        val parsed = LocalDate.parse(log.date)
                        parsed.year == today.year && parsed.month == today.month
                    } catch (_: Exception) {
                        true
                    }
                }
            }

            // Search query filter (matches student name or notes)
            val queryMatches = if (query.isBlank()) {
                true
            } else {
                log.studentName.contains(query, ignoreCase = true) ||
                        log.notes.contains(query, ignoreCase = true)
            }

            dateMatches && queryMatches
        }

        val totalMinutes = filtered.sumOf { it.durationMinutes }
        val hours = totalMinutes / 60
        val mins = totalMinutes % 60
        val formattedDuration = when {
            hours > 0 && mins > 0 -> "${hours}h ${mins}m"
            hours > 0 -> "${hours}h"
            else -> "${mins}m"
        }

        val uniqueStudents = filtered.map { it.studentName }.filter { it.isNotBlank() }.distinct().size

        TutorUiState(
            logs = logs,
            filteredLogs = filtered,
            studentSuggestions = studentSuggestions,
            searchQuery = query,
            filterMode = filter,
            isAddEditOpen = addEditOpen,
            editingLog = editing,
            isShareOpen = shareOpen,
            shareScope = scope,
            totalMinutes = totalMinutes,
            formattedTotalTime = formattedDuration,
            uniqueStudentCount = uniqueStudents
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TutorUiState()
    )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refresh()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterMode(mode: DateFilterMode) {
        _filterMode.value = mode
    }

    fun setShareScope(scope: ShareScope) {
        _shareScope.value = scope
    }

    fun openAddDialog() {
        _editingLog.value = null
        _isAddEditOpen.value = true
    }

    fun openEditDialog(log: TutorLog) {
        _editingLog.value = log
        _isAddEditOpen.value = true
    }

    fun closeAddEditDialog() {
        _editingLog.value = null
        _isAddEditOpen.value = false
    }

    fun openShareDialog() {
        _isShareOpen.value = true
    }

    fun closeShareDialog() {
        _isShareOpen.value = false
    }

    fun saveLog(
        id: Long = 0L,
        studentName: String,
        date: String,
        startTime: String,
        endTime: String,
        notes: String
    ) {
        viewModelScope.launch {
            val log = TutorLog(
                id = id,
                studentName = studentName.trim(),
                date = date,
                startTime = startTime,
                endTime = endTime,
                notes = notes.trim()
            )
            if (id == 0L) {
                repository.addLog(log)
            } else {
                repository.updateLog(log)
            }
            closeAddEditDialog()
        }
    }

    fun deleteLog(id: Long) {
        viewModelScope.launch {
            repository.deleteLog(id)
        }
    }

    /**
     * Prepares formatted text based on the chosen scope.
     * When scope is UP_TO_TODAY, it includes all logs saved up to today.
     */
    fun getShareText(scope: ShareScope): String {
        val todayStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val allLogs = uiState.value.logs

        return when (scope) {
            ShareScope.UP_TO_TODAY -> {
                val upToTodayLogs = allLogs.filter { it.date <= todayStr }
                repository.formatLedgerForSharing(upToTodayLogs, "ALL LOGS SAVED UP TO TODAY ($todayStr)")
            }
            ShareScope.ALL_TIME -> {
                repository.formatLedgerForSharing(allLogs, "COMPLETE TUTOR HISTORY")
            }
            ShareScope.FILTERED -> {
                val filtered = uiState.value.filteredLogs
                val filterLabel = uiState.value.filterMode.label.uppercase()
                repository.formatLedgerForSharing(filtered, "FILTERED: $filterLabel")
            }
        }
    }
}
