package com.mightypen.tutorlog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mightypen.tutorlog.ui.theme.Caribbean
import com.mightypen.tutorlog.ui.theme.InkDark
import com.mightypen.tutorlog.ui.theme.PaperCream
import com.mightypen.tutorlog.ui.theme.Sinopia
import com.mightypen.tutorlog.ui.theme.SinopiaContainer
import com.mightypen.tutorlog.ui.theme.Vanilla
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetroDatePicker(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val initialMillis = initialDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selected = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        onDateSelected(selected)
                    }
                    onDismiss()
                }
            ) {
                Text(
                    "CONFIRM",
                    color = Sinopia,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "CANCEL",
                    color = Caribbean,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = PaperCream
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = PaperCream,
                titleContentColor = Caribbean,
                headlineContentColor = Sinopia,
                weekdayContentColor = Caribbean,
                subheadContentColor = InkDark,
                yearContentColor = InkDark,
                currentYearContentColor = Sinopia,
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = Sinopia,
                dayContentColor = InkDark,
                selectedDayContainerColor = Sinopia,
                selectedDayContentColor = Color.White,
                todayContentColor = Sinopia,
                todayDateBorderColor = Caribbean
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetroTimePicker(
    initialHour: Int,
    initialMinute: Int,
    is24Hour: Boolean = false,
    titleText: String = "SELECT TIME",
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour
    )

    Dialog(onDismissRequest = onDismiss) {
        RetroCard(
            backgroundColor = PaperCream,
            borderColor = Caribbean,
            shadowColor = Caribbean.copy(alpha = 0.4f),
            cornerRadius = 14.dp,
            borderWidth = 2.dp,
            shadowOffset = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = titleText,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Caribbean,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Vanilla,
                        clockDialSelectedContentColor = Color.White,
                        clockDialUnselectedContentColor = InkDark,
                        selectorColor = Sinopia,
                        containerColor = PaperCream,
                        periodSelectorBorderColor = Caribbean,
                        periodSelectorSelectedContainerColor = Caribbean,
                        periodSelectorUnselectedContainerColor = Color.Transparent,
                        periodSelectorSelectedContentColor = Color.White,
                        periodSelectorUnselectedContentColor = Caribbean,
                        timeSelectorSelectedContainerColor = SinopiaContainer,
                        timeSelectorUnselectedContainerColor = Vanilla,
                        timeSelectorSelectedContentColor = Sinopia,
                        timeSelectorUnselectedContentColor = InkDark
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            "CANCEL",
                            color = Caribbean,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(
                        onClick = {
                            onTimeSelected(timePickerState.hour, timePickerState.minute)
                            onDismiss()
                        }
                    ) {
                        Text(
                            "CONFIRM",
                            color = Sinopia,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
