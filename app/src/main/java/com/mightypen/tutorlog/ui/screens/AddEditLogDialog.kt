package com.mightypen.tutorlog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mightypen.tutorlog.model.TutorLog
import com.mightypen.tutorlog.ui.components.DurationPill
import com.mightypen.tutorlog.ui.components.RetroButton
import com.mightypen.tutorlog.ui.components.RetroCard
import com.mightypen.tutorlog.ui.components.RetroDatePicker
import com.mightypen.tutorlog.ui.components.RetroPill
import com.mightypen.tutorlog.ui.components.RetroTimePicker
import com.mightypen.tutorlog.ui.theme.Caribbean
import com.mightypen.tutorlog.ui.theme.CaribbeanContainer
import com.mightypen.tutorlog.ui.theme.InkDark
import com.mightypen.tutorlog.ui.theme.InkMuted
import com.mightypen.tutorlog.ui.theme.PaperCream
import com.mightypen.tutorlog.ui.theme.Sinopia
import com.mightypen.tutorlog.ui.theme.Vanilla
import com.mightypen.tutorlog.ui.theme.VanillaDark
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AddEditLogDialog(
    initialLog: TutorLog?,
    studentSuggestions: List<String>,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        studentName: String,
        date: String,
        startTime: String,
        endTime: String,
        notes: String
    ) -> Unit
) {
    val today = remember { LocalDate.now() }
    val now = remember { LocalTime.now() }

    var studentName by remember { mutableStateOf(initialLog?.studentName ?: "") }
    var selectedDate by remember {
        mutableStateOf(
            initialLog?.let {
                try { LocalDate.parse(it.date) } catch (_: Exception) { today }
            } ?: today
        )
    }

    // Default times: start at round hour (e.g. 14:00), end 1 hour later (15:00)
    var startHour by remember {
        mutableStateOf(
            initialLog?.let {
                try { LocalTime.parse(it.startTime).hour } catch (_: Exception) { now.hour }
            } ?: now.hour
        )
    }
    var startMinute by remember {
        mutableStateOf(
            initialLog?.let {
                try { LocalTime.parse(it.startTime).minute } catch (_: Exception) { 0 }
            } ?: 0
        )
    }

    var endHour by remember {
        mutableStateOf(
            initialLog?.let {
                try { LocalTime.parse(it.endTime).hour } catch (_: Exception) { (now.hour + 1) % 24 }
            } ?: ((now.hour + 1) % 24)
        )
    }
    var endMinute by remember {
        mutableStateOf(
            initialLog?.let {
                try { LocalTime.parse(it.endTime).minute } catch (_: Exception) { 0 }
            } ?: 0
        )
    }

    var notes by remember { mutableStateOf(initialLog?.notes ?: "") }
    var studentNameError by remember { mutableStateOf(false) }

    // Dialog pickers state
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // Calculated duration
    val durationText by remember {
        derivedStateOf {
            val startTotal = startHour * 60 + startMinute
            val endTotal = endHour * 60 + endMinute
            val diff = if (endTotal >= startTotal) endTotal - startTotal else (24 * 60 - startTotal) + endTotal
            val h = diff / 60
            val m = diff % 60
            when {
                h > 0 && m > 0 -> "${h}h ${m}m"
                h > 0 -> "${h}h"
                else -> "${m}m"
            }
        }
    }

    val formattedStartTime by remember {
        derivedStateOf {
            val t = LocalTime.of(startHour, startMinute)
            t.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
        }
    }

    val formattedEndTime by remember {
        derivedStateOf {
            val t = LocalTime.of(endHour, endMinute)
            t.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            RetroCard(
                backgroundColor = PaperCream,
                borderColor = Caribbean,
                shadowColor = Caribbean.copy(alpha = 0.45f),
                cornerRadius = 14.dp,
                borderWidth = 2.dp,
                shadowOffset = 5.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header Stamp
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (initialLog == null) "NEW SESSION LOG" else "EDIT SESSION LOG",
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Caribbean,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Record session hours & student activities",
                                fontSize = 12.sp,
                                color = InkMuted
                            )
                        }
                        DurationPill(durationText = durationText)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Student / Subject Name
                    Text(
                        text = "STUDENT / SUBJECT *",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Caribbean,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = {
                            studentName = it
                            if (it.isNotBlank()) studentNameError = false
                        },
                        placeholder = { Text("e.g. Alex (Calculus II) or Emma", color = InkMuted.copy(alpha = 0.6f)) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Caribbean)
                        },
                        isError = studentNameError,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Caribbean,
                            unfocusedBorderColor = VanillaDark,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = InkDark,
                            unfocusedTextColor = InkDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (studentNameError) {
                        Text(
                            text = "Please enter student name or subject",
                            color = Sinopia,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }

                    // Autocomplete Suggestions Chips if available
                    if (studentSuggestions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            studentSuggestions.forEach { name ->
                                Box(
                                    modifier = Modifier
                                        .background(CaribbeanContainer, RoundedCornerShape(12.dp))
                                        .border(1.dp, Caribbean.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                        .clickable {
                                            studentName = name
                                            studentNameError = false
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "+ $name",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Caribbean
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date Picker Trigger
                    Text(
                        text = "SESSION DATE",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Caribbean,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, VanillaDark, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 14.dp, vertical = 13.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Caribbean, modifier = Modifier.padding(end = 10.dp))
                                Text(
                                    text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy", Locale.getDefault())),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = InkDark
                                )
                            }
                            RetroPill(
                                text = selectedDate.format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault())).uppercase(),
                                backgroundColor = Vanilla,
                                textColor = Caribbean,
                                borderColor = Caribbean.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Time Row (Start Time & End Time)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Start Time Trigger
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "START TIME",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Caribbean,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .border(1.dp, VanillaDark, RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showStartTimePicker = true }
                                    .padding(horizontal = 12.dp, vertical = 12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Sinopia, modifier = Modifier.padding(end = 8.dp))
                                    Text(
                                        text = formattedStartTime,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = InkDark
                                    )
                                }
                            }
                        }

                        // End Time Trigger
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "END TIME",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Caribbean,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .border(1.dp, VanillaDark, RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showEndTimePicker = true }
                                    .padding(horizontal = 12.dp, vertical = 12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Caribbean, modifier = Modifier.padding(end = 8.dp))
                                    Text(
                                        text = formattedEndTime,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = InkDark
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Additional Notes
                    Text(
                        text = "NOTES & ACTIVITIES",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Caribbean,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = {
                            Text(
                                "e.g. Covered Chapter 4 kinematics, worked through practice problem set 3, assigned homework...",
                                color = InkMuted.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Caribbean,
                            unfocusedBorderColor = VanillaDark,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = InkDark,
                            unfocusedTextColor = InkDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 160.dp),
                        maxLines = 6
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = "CANCEL",
                                color = Caribbean,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        RetroButton(
                            text = if (initialLog == null) "SAVE LOG" else "UPDATE LOG",
                            backgroundColor = Sinopia,
                            contentColor = Color.White,
                            borderColor = InkDark,
                            shadowColor = Caribbean.copy(alpha = 0.5f),
                            onClick = {
                                if (studentName.trim().isBlank()) {
                                    studentNameError = true
                                } else {
                                    val dateStr = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                                    val startStr = String.format(Locale.US, "%02d:%02d", startHour, startMinute)
                                    val endStr = String.format(Locale.US, "%02d:%02d", endHour, endMinute)
                                    onSave(
                                        initialLog?.id ?: 0L,
                                        studentName,
                                        dateStr,
                                        startStr,
                                        endStr,
                                        notes
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        RetroDatePicker(
            initialDate = selectedDate,
            onDateSelected = { selectedDate = it },
            onDismiss = { showDatePicker = false }
        )
    }

    // Start Time Picker Dialog
    if (showStartTimePicker) {
        RetroTimePicker(
            initialHour = startHour,
            initialMinute = startMinute,
            titleText = "SESSION START TIME",
            onTimeSelected = { h, m ->
                startHour = h
                startMinute = m
            },
            onDismiss = { showStartTimePicker = false }
        )
    }

    // End Time Picker Dialog
    if (showEndTimePicker) {
        RetroTimePicker(
            initialHour = endHour,
            initialMinute = endMinute,
            titleText = "SESSION END TIME",
            onTimeSelected = { h, m ->
                endHour = h
                endMinute = m
            },
            onDismiss = { showEndTimePicker = false }
        )
    }
}
