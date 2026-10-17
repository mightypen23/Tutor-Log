package com.mightypen.tutorlog

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mightypen.tutorlog.ui.TutorViewModel
import com.mightypen.tutorlog.ui.screens.AddEditLogDialog
import com.mightypen.tutorlog.ui.screens.HomeScreen
import com.mightypen.tutorlog.ui.screens.ShareDialog
import com.mightypen.tutorlog.ui.theme.TutorLogTheme
import com.mightypen.tutorlog.ui.theme.Vanilla

class MainActivity : ComponentActivity() {
    private val viewModel: TutorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TutorLogTheme {
                val uiState by viewModel.uiState.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Vanilla
                ) {
                    HomeScreen(
                        uiState = uiState,
                        onSearchChanged = { viewModel.setSearchQuery(it) },
                        onFilterChanged = { viewModel.setFilterMode(it) },
                        onOpenAdd = { viewModel.openAddDialog() },
                        onOpenEdit = { viewModel.openEditDialog(it) },
                        onDeleteLog = { viewModel.deleteLog(it) },
                        onOpenShare = { viewModel.openShareDialog() }
                    )

                    // Add/Edit Dialog
                    if (uiState.isAddEditOpen) {
                        AddEditLogDialog(
                            initialLog = uiState.editingLog,
                            studentSuggestions = uiState.studentSuggestions,
                            onDismiss = { viewModel.closeAddEditDialog() },
                            onSave = { id, student, date, start, end, notes ->
                                viewModel.saveLog(id, student, date, start, end, notes)
                            }
                        )
                    }

                    // Share Dialog with native app chooser
                    if (uiState.isShareOpen) {
                        ShareDialog(
                            initialScope = uiState.shareScope,
                            onGetShareText = { scope ->
                                viewModel.getShareText(scope)
                            },
                            onDismiss = { viewModel.closeShareDialog() },
                            onTriggerShare = { text ->
                                triggerNativeShare(text)
                            }
                        )
                    }
                }
            }
        }
    }

    /**
     * Triggers the Android system share sheet (Intent.createChooser)
     * which prompts the user to select which app to share to (e.g., Samsung Notes,
     * Google Keep, WhatsApp, Gmail, etc.)
     */
    private fun triggerNativeShare(text: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Tutor Activity Log")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooserIntent = Intent.createChooser(sendIntent, "Share Tutor Log to...")
        startActivity(chooserIntent)
    }
}
