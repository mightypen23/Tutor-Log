package com.mightypen.tutorlog.data

import android.content.Context
import com.mightypen.tutorlog.model.TutorLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class TutorRepository(context: Context) {
    private val dbHelper = TutorDatabaseHelper(context.applicationContext)

    private val _logsFlow = MutableStateFlow<List<TutorLog>>(emptyList())
    val logsFlow: StateFlow<List<TutorLog>> = _logsFlow.asStateFlow()

    private val _studentNamesFlow = MutableStateFlow<List<String>>(emptyList())
    val studentNamesFlow: StateFlow<List<String>> = _studentNamesFlow.asStateFlow()

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val logs = dbHelper.getAllLogs()
        val students = dbHelper.getDistinctStudentNames()
        _logsFlow.value = logs
        _studentNamesFlow.value = students
    }

    suspend fun addLog(log: TutorLog): Long = withContext(Dispatchers.IO) {
        val id = dbHelper.insertLog(log)
        refresh()
        id
    }

    suspend fun updateLog(log: TutorLog): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.updateLog(log)
        refresh()
        rows > 0
    }

    suspend fun deleteLog(id: Long): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.deleteLog(id)
        refresh()
        rows > 0
    }

    suspend fun getLogsUpToDate(maxDate: String): List<TutorLog> = withContext(Dispatchers.IO) {
        dbHelper.getLogsUpToDate(maxDate)
    }

    /**
     * Formats tutoring logs into a professional, cleanly aligned modern retro ledger summary
     * specifically tailored for sharing to notes apps (such as Samsung Notes, Google Keep),
     * messaging, or email.
     */
    fun formatLedgerForSharing(
        logs: List<TutorLog>,
        periodTitle: String = "ALL LOGS UP TO TODAY"
    ): String {
        if (logs.isEmpty()) {
            return "TUTOR LOG REPORT\nNo tutoring sessions recorded."
        }

        val totalMinutes = logs.sumOf { it.durationMinutes }
        val totalHours = totalMinutes / 60
        val remainingMinutes = totalMinutes % 60
        val durationSummary = when {
            totalHours > 0 && remainingMinutes > 0 -> "${totalHours}h ${remainingMinutes}m"
            totalHours > 0 -> "${totalHours}h"
            else -> "${remainingMinutes}m"
        }

        val uniqueStudents = logs.map { it.studentName }.filter { it.isNotBlank() }.distinct().size
        val todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault()))

        val sb = StringBuilder()
        sb.append("╔══════════════════════════════════════════╗\n")
        sb.append("║           TUTOR ACTIVITY LOG             ║\n")
        sb.append("╚══════════════════════════════════════════╝\n")
        sb.append("Period: $periodTitle\n")
        sb.append("Exported: $todayStr\n")
        sb.append("Total Sessions: ${logs.size}\n")
        sb.append("Total Hours Logged: $durationSummary\n")
        if (uniqueStudents > 0) {
            sb.append("Students: $uniqueStudents\n")
        }
        sb.append("──────────────────────────────────────────\n")
        sb.append("SESSION DETAILS:\n\n")

        logs.forEachIndexed { index, log ->
            val num = (index + 1).toString().padStart(2, '0')
            sb.append("[$num] ${log.formattedDate} (${log.dayOfWeek})\n")
            sb.append("     Student: ${log.studentName.ifBlank { "General Tutoring" }}\n")
            sb.append("     Time:    ${log.formattedTimeRange} (${log.formattedDuration})\n")
            if (log.notes.isNotBlank()) {
                val indentedNotes = log.notes.trim().lines().joinToString("\n              ")
                sb.append("     Notes:   $indentedNotes\n")
            }
            sb.append("\n")
        }

        sb.append("──────────────────────────────────────────\n")
        sb.append("Generated with Tutor Log\n")
        return sb.toString()
    }
}
