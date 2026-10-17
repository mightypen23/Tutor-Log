package com.mightypen.tutorlog.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val RetroColorScheme = lightColorScheme(
    primary = Sinopia,
    onPrimary = Color.White,
    primaryContainer = SinopiaContainer,
    onPrimaryContainer = SinopiaDark,

    secondary = Caribbean,
    onSecondary = Color.White,
    secondaryContainer = CaribbeanContainer,
    onSecondaryContainer = CaribbeanDark,

    tertiary = RetroGold,
    onTertiary = Color.White,

    background = Vanilla,
    onBackground = InkDark,

    surface = PaperCream,
    onSurface = InkDark,
    surfaceVariant = VanillaLight,
    onSurfaceVariant = InkMuted,

    outline = Caribbean,
    outlineVariant = VanillaDark
)

val RetroShapes = Shapes(
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(10.dp),
    large = RoundedCornerShape(16.dp)
)

@Composable
fun TutorLogTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = RetroColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Caribbean.toArgb()
            window.navigationBarColor = Vanilla.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RetroTypography,
        shapes = RetroShapes,
        content = content
    )
}
