package com.mightypen.tutorlog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mightypen.tutorlog.ui.theme.Caribbean
import com.mightypen.tutorlog.ui.theme.InkDark
import com.mightypen.tutorlog.ui.theme.PaperCream
import com.mightypen.tutorlog.ui.theme.Sinopia

/**
 * Modern retro card featuring a crisp solid outline and offset drop shadow.
 */
@Composable
fun RetroCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = PaperCream,
    borderColor: Color = Caribbean,
    shadowColor: Color = Caribbean.copy(alpha = 0.35f),
    cornerRadius: Dp = 10.dp,
    borderWidth: Dp = 1.5.dp,
    shadowOffset: Dp = 3.5.dp,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(modifier = modifier) {
        // Offset solid shadow layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(shadowColor, shape)
        )

        // Front card surface
        Box(
            modifier = Modifier
                .background(backgroundColor, shape)
                .border(borderWidth, borderColor, shape)
                .clip(shape)
        ) {
            content()
        }
    }
}

/**
 * Modern retro tactile button with animated depress effect on press.
 */
@Composable
fun RetroButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Sinopia,
    contentColor: Color = Color.White,
    borderColor: Color = InkDark,
    shadowColor: Color = InkDark,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shape = RoundedCornerShape(8.dp)

    val currentOffset = if (isPressed) 1.dp else 3.5.dp

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Shadow underneath
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 3.5.dp, y = 3.5.dp)
                .background(shadowColor, shape)
        )

        // Button face that depresses when pressed
        Box(
            modifier = Modifier
                .offset(
                    x = if (isPressed) 2.5.dp else 0.dp,
                    y = if (isPressed) 2.5.dp else 0.dp
                )
                .background(backgroundColor, shape)
                .border(1.5.dp, borderColor, shape)
                .clip(shape)
                .padding(horizontal = 16.dp, vertical = 11.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.layout.Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(end = 6.dp))
                }
                Text(
                    text = text,
                    color = contentColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
