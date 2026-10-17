package com.mightypen.tutorlog.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mightypen.tutorlog.ui.ShareScope
import com.mightypen.tutorlog.ui.components.RetroButton
import com.mightypen.tutorlog.ui.components.RetroCard
import com.mightypen.tutorlog.ui.components.RetroPill
import com.mightypen.tutorlog.ui.theme.Caribbean
import com.mightypen.tutorlog.ui.theme.CaribbeanContainer
import com.mightypen.tutorlog.ui.theme.InkDark
import com.mightypen.tutorlog.ui.theme.InkMuted
import com.mightypen.tutorlog.ui.theme.PaperCream
import com.mightypen.tutorlog.ui.theme.Sinopia
import com.mightypen.tutorlog.ui.theme.SinopiaContainer
import com.mightypen.tutorlog.ui.theme.Vanilla
import com.mightypen.tutorlog.ui.theme.VanillaDark

@Composable
fun ShareDialog(
    initialScope: ShareScope,
    onGetShareText: (ShareScope) -> String,
    onDismiss: () -> Unit,
    onTriggerShare: (text: String) -> Unit
) {
    val context = LocalContext.current
    var selectedScope by remember { mutableStateOf(initialScope) }
    val shareText = remember(selectedScope) { onGetShareText(selectedScope) }

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
                shadowColor = Caribbean.copy(alpha = 0.5f),
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
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SHARE TUTOR LEDGER",
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Caribbean,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Export report to Samsung Notes, Keep, etc.",
                                fontSize = 12.sp,
                                color = InkMuted
                            )
                        }
                        RetroPill(
                            text = "SHARE SHEET",
                            backgroundColor = SinopiaContainer,
                            textColor = Sinopia,
                            borderColor = Sinopia.copy(alpha = 0.5f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Scope Selection
                    Text(
                        text = "SELECT LOG RANGE TO EXPORT:",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Caribbean,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ShareScope.values().forEach { scope ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedScope = scope }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedScope == scope),
                                onClick = { selectedScope = scope },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Sinopia,
                                    unselectedColor = Caribbean
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = scope.label,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedScope == scope) FontWeight.Bold else FontWeight.Medium,
                                    color = InkDark
                                )
                                if (scope == ShareScope.UP_TO_TODAY) {
                                    Text(
                                        text = "Includes all tutoring activities saved up to today",
                                        fontSize = 11.sp,
                                        color = InkMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ledger text preview box
                    Text(
                        text = "LEDGER REPORT PREVIEW:",
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
                            .heightIn(min = 120.dp, max = 220.dp)
                            .background(Vanilla.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                            .border(1.dp, VanillaDark, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = shareText,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = InkDark,
                            lineHeight = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Copy to clipboard option
                        TextButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Tutor Log Report", shareText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Ledger report copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Caribbean, modifier = Modifier.padding(end = 6.dp))
                            Text(
                                text = "COPY TEXT",
                                color = Caribbean,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Main Share button (triggers Android native share sheet)
                        RetroButton(
                            text = "SHARE TO APPS",
                            leadingIcon = {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                            },
                            backgroundColor = Sinopia,
                            contentColor = Color.White,
                            borderColor = InkDark,
                            shadowColor = Caribbean.copy(alpha = 0.5f),
                            onClick = {
                                onTriggerShare(shareText)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}
