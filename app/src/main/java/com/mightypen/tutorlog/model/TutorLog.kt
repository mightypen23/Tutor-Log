package com.mightypen.tutorlog.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Data model representing a single tutoring activity session.
 */
data class TutorLog(
    val id: Long = 0L,
    val studentName: String,
    val date: String,       // YYYY-MM-DD
    val startTime: String,  // HH:mm (24-hour)
    val endTime: String,    // HH:mm (24-hour)
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Calculates duration in total minutes between startTime and endTime.
     */
    val durationMinutes: Long
        get() = try {
            val start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"))
            val end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"))
            val startMinutes = start.hour * 60L + start.minute
            val endMinutes = end.hour * 60L + end.minute
            if (endMinutes >= startMinutes) {
                endMinutes - startMinutes
            } else {
                // If crossing midnight
                (24 * 60L - startMinutes) + endMinutes
            }
        } catch (_: Exception) {
            0L
        }

    /**
     * Formats duration into a human-readable string (e.g., "1h 30m" or "45m").
     */
    val formattedDuration: String
        get() {
            val total = durationMinutes
            val hours = total / 60
            val minutes = total % 60
            return when {
                hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
                hours > 0 -> "${hours}h"
                else -> "${minutes}m"
            }
        }

    /**
     * Formats date nicely (e.g. "Oct 17, 2026").
     */
    val formattedDate: String
        get() = try {
            val parsed = LocalDate.parse(date)
            parsed.format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault()))
        } catch (_: Exception) {
            date
        }

    /**
     * Day of week abbreviation (e.g. "SAT", "MON").
     */
    val dayOfWeek: String
        get() = try {
            val parsed = LocalDate.parse(date)
            parsed.format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault())).uppercase()
        } catch (_: Exception) {
            ""
        }

    /**
     * Formats 24h start/end into friendly 12h range (e.g. "2:30 PM - 4:00 PM").
     */
    val formattedTimeRange: String
        get() = try {
            val s = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"))
            val e = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"))
            val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
            "${s.format(formatter)} – ${e.format(formatter)}"
        } catch (_: Exception) {
            "$startTime - $endTime"
        }
}
