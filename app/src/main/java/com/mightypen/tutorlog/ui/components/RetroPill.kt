package com.mightypen.tutorlog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mightypen.tutorlog.ui.theme.Caribbean
import com.mightypen.tutorlog.ui.theme.CaribbeanContainer
import com.mightypen.tutorlog.ui.theme.Sinopia
import com.mightypen.tutorlog.ui.theme.SinopiaContainer

@Composable
fun RetroPill(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = CaribbeanContainer,
    textColor: Color = Caribbean,
    borderColor: Color = Caribbean.copy(alpha = 0.5f),
    fontSize: TextUnit = 11.sp,
    horizontalPadding: Dp = 8.dp,
    verticalPadding: Dp = 4.dp
) {
    val shape = RoundedCornerShape(50)

    Box(
        modifier = modifier
            .background(backgroundColor, shape)
            .border(1.dp, borderColor, shape)
            .clip(shape)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.4.sp
        )
    }
}

@Composable
fun DurationPill(
    durationText: String,
    modifier: Modifier = Modifier
) {
    RetroPill(
        text = durationText,
        modifier = modifier,
        backgroundColor = SinopiaContainer,
        textColor = Sinopia,
        borderColor = Sinopia.copy(alpha = 0.6f),
        fontSize = 11.sp
    )
}
