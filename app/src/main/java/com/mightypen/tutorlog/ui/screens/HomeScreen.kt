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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mightypen.tutorlog.model.TutorLog
import com.mightypen.tutorlog.ui.DateFilterMode
import com.mightypen.tutorlog.ui.TutorUiState
import com.mightypen.tutorlog.ui.components.DurationPill
import com.mightypen.tutorlog.ui.components.RetroButton
import com.mightypen.tutorlog.ui.components.RetroCard
import com.mightypen.tutorlog.ui.components.RetroPill
import com.mightypen.tutorlog.ui.theme.Caribbean
import com.mightypen.tutorlog.ui.theme.CaribbeanContainer
import com.mightypen.tutorlog.ui.theme.CaribbeanDark
import com.mightypen.tutorlog.ui.theme.InkDark
import com.mightypen.tutorlog.ui.theme.InkMuted
import com.mightypen.tutorlog.ui.theme.PaperCream
import com.mightypen.tutorlog.ui.theme.Sinopia
import com.mightypen.tutorlog.ui.theme.SinopiaContainer
import com.mightypen.tutorlog.ui.theme.Vanilla
import com.mightypen.tutorlog.ui.theme.VanillaDark

@Composable
fun HomeScreen(
    uiState: TutorUiState,
    onSearchChanged: (String) -> Unit,
    onFilterChanged: (DateFilterMode) -> Unit,
    onOpenAdd: () -> Unit,
    onOpenEdit: (TutorLog) -> Unit,
    onDeleteLog: (Long) -> Unit,
    onOpenShare: () -> Unit
) {
    var logToDelete by remember { mutableStateOf<TutorLog?>(null) }

    Scaffold(
        containerColor = Vanilla,
        floatingActionButton = {
            // Modern Retro FAB
            Box {
                // Shadow
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(x = 3.dp, y = 3.dp)
                        .background(InkDark, RoundedCornerShape(12.dp))
                )
                Box(
                    modifier = Modifier
                        .background(Sinopia, RoundedCornerShape(12.dp))
                        .border(1.5.dp, InkDark, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenAdd() }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LOG SESSION",
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Retro Top Bar
            RetroHeader(
                totalLogsCount = uiState.logs.size,
                onOpenShare = onOpenShare
            )

            // Content Column
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Stats Dashboard Banner
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    StatsLedgerDashboard(
                        totalHours = uiState.formattedTotalTime,
                        totalSessions = uiState.filteredLogs.size,
                        uniqueStudents = uiState.uniqueStudentCount
                    )
                }

                // Search & Filter Section
                item {
                    FilterAndSearchBar(
                        searchQuery = uiState.searchQuery,
                        currentFilter = uiState.filterMode,
                        onSearchChanged = onSearchChanged,
                        onFilterChanged = onFilterChanged
                    )
                }

                // List header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECORDED SESSIONS (${uiState.filteredLogs.size})",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Caribbean,
                            letterSpacing = 0.8.sp
                        )
                        if (uiState.searchQuery.isNotBlank() || uiState.filterMode != DateFilterMode.ALL) {
                            Text(
                                text = "Filtered",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Sinopia,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // If no logs found
                if (uiState.filteredLogs.isEmpty()) {
                    item {
                        EmptyStateView(
                            hasAnyLogs = uiState.logs.isNotEmpty(),
                            onClearFilters = {
                                onSearchChanged("")
                                onFilterChanged(DateFilterMode.ALL)
                            },
                            onAddLog = onOpenAdd
                        )
                    }
                } else {
                    // Items List
                    items(uiState.filteredLogs, key = { it.id }) { log ->
                        TutorLogCard(
                            log = log,
                            onEdit = { onOpenEdit(log) },
                            onDelete = { logToDelete = log }
                        )
                    }
                }

                // Bottom padding for FAB clearance
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Delete Confirmation Dialog
    logToDelete?.let { log ->
        AlertDialog(
            onDismissRequest = { logToDelete = null },
            title = {
                Text(
                    text = "DELETE ENTRY?",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Sinopia
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove the tutoring log for ${log.studentName} on ${log.formattedDate}?",
                    color = InkDark,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteLog(log.id)
                        logToDelete = null
                    }
                ) {
                    Text("DELETE", color = Sinopia, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            },
            dismissButton = {
                TextButton(onClick = { logToDelete = null }) {
                    Text("CANCEL", color = Caribbean, fontFamily = FontFamily.Monospace)
                }
            },
            containerColor = PaperCream
        )
    }
}

/**
 * Wes Anderson styled Header with vintage stamp and Share action.
 */
@Composable
private fun RetroHeader(
    totalLogsCount: Int,
    onOpenShare: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Caribbean)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Sinopia, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TUTOR LOG",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Vanilla,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "OFFICIAL ACTIVITY LEDGER",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = Vanilla.copy(alpha = 0.8f),
                    letterSpacing = 1.2.sp
                )
            }

            // Share Action Button
            RetroButton(
                text = "SHARE",
                leadingIcon = {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                },
                backgroundColor = Sinopia,
                contentColor = Color.White,
                borderColor = VanillaDark,
                shadowColor = InkDark,
                onClick = onOpenShare
            )
        }
    }
}

/**
 * Vintage 3-cell metric summary dashboard.
 */
@Composable
private fun StatsLedgerDashboard(
    totalHours: String,
    totalSessions: Int,
    uniqueStudents: Int
) {
    RetroCard(
        backgroundColor = PaperCream,
        borderColor = Caribbean,
        shadowColor = Caribbean.copy(alpha = 0.35f),
        cornerRadius = 10.dp,
        borderWidth = 1.5.dp,
        shadowOffset = 3.5.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stat 1: Total Hours
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = totalHours,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Sinopia
                )
                Text(
                    text = "HOURS LOGGED",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    color = Caribbean,
                    letterSpacing = 0.5.sp
                )
            }

            // Vertical ledger divider
            Box(
                modifier = Modifier
                    .height(32.dp)
                    .width(1.dp)
                    .background(VanillaDark)
            )

            // Stat 2: Total Sessions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = totalSessions.toString(),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Caribbean
                )
                Text(
                    text = "SESSIONS",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    color = Caribbean,
                    letterSpacing = 0.5.sp
                )
            }

            // Vertical ledger divider
            Box(
                modifier = Modifier
                    .height(32.dp)
                    .width(1.dp)
                    .background(VanillaDark)
            )

            // Stat 3: Unique Students
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = uniqueStudents.toString(),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Caribbean
                )
                Text(
                    text = "STUDENTS",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    color = Caribbean,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

/**
 * Search and Filter bar with tactile retro chips.
 */
@Composable
private fun FilterAndSearchBar(
    searchQuery: String,
    currentFilter: DateFilterMode,
    onSearchChanged: (String) -> Unit,
    onFilterChanged: (DateFilterMode) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Search Input Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChanged,
            placeholder = { Text("Search by student or notes...", color = InkMuted.copy(alpha = 0.7f), fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Caribbean, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChanged("") }) {
                        Icon(Icons.Default.Clear, contentDescription = null, tint = Caribbean, modifier = Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Caribbean,
                unfocusedBorderColor = Caribbean.copy(alpha = 0.4f),
                focusedContainerColor = PaperCream,
                unfocusedContainerColor = PaperCream,
                focusedTextColor = InkDark,
                unfocusedTextColor = InkDark
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Date Filter Pills Scrollable Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DateFilterMode.values().forEach { mode ->
                val isSelected = (currentFilter == mode)
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isSelected) Caribbean else PaperCream,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) CaribbeanDark else Caribbean.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onFilterChanged(mode) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = mode.label.uppercase(),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Vanilla else Caribbean,
                        letterSpacing = 0.4.sp
                    )
                }
            }
        }
    }
}

/**
 * Individual Tutor Log Ledger Card.
 */
@Composable
private fun TutorLogCard(
    log: TutorLog,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    RetroCard(
        backgroundColor = PaperCream,
        borderColor = Caribbean,
        shadowColor = Caribbean.copy(alpha = 0.35f),
        cornerRadius = 10.dp,
        borderWidth = 1.5.dp,
        shadowOffset = 3.5.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Student name, Date stamp, Duration pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = log.studentName.ifBlank { "General Tutoring" },
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = InkDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${log.formattedDate} • ${log.dayOfWeek}",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Caribbean,
                        letterSpacing = 0.5.sp
                    )
                }

                DurationPill(durationText = log.formattedDuration)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Time range row with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Vanilla.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = Sinopia,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = log.formattedTimeRange,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = InkDark
                )
            }

            // Notes if provided
            if (log.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = log.notes,
                    fontSize = 13.sp,
                    color = InkDark,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom action row: Edit & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit entry",
                        tint = Caribbean,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete entry",
                        tint = Sinopia,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Empty state when no sessions have been logged yet or filter matches none.
 */
@Composable
private fun EmptyStateView(
    hasAnyLogs: Boolean,
    onClearFilters: () -> Unit,
    onAddLog: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            // Retro emblem
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(CaribbeanContainer, CircleShape)
                    .border(2.dp, Caribbean, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = Caribbean,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (hasAnyLogs) "NO MATCHING SESSIONS" else "NO SESSIONS LOGGED YET",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Caribbean,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (hasAnyLogs) {
                    "No sessions match the current search or date filter."
                } else {
                    "Keep track of student sessions, hours, and notes locally on your phone."
                },
                fontSize = 13.sp,
                color = InkMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (hasAnyLogs) {
                RetroButton(
                    text = "CLEAR FILTERS",
                    backgroundColor = Caribbean,
                    contentColor = Vanilla,
                    borderColor = CaribbeanDark,
                    shadowColor = InkDark,
                    onClick = onClearFilters
                )
            } else {
                RetroButton(
                    text = "+ LOG FIRST SESSION",
                    backgroundColor = Sinopia,
                    contentColor = Color.White,
                    borderColor = InkDark,
                    shadowColor = Caribbean.copy(alpha = 0.5f),
                    onClick = onAddLog
                )
            }
        }
    }
}
