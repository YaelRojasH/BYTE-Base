
package com.iyrh.bytebase.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Tema claro de BYTE Base
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0B63CE),
    onPrimary = Color(0xFFFFFFFF),

    secondary = Color(0xFF084A9B),
    onSecondary = Color(0xFFFFFFFF),

    background = Color(0xFFF5F8FC),
    onBackground = Color(0xFF17365D),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF17365D),

    surfaceVariant = Color(0xFFE8F2FF),
    onSurfaceVariant = Color(0xFF64748B),

    outline = Color(0xFFD5E2F2)
)

// Tema oscuro de BYTE Base
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8AB4F8),
    onPrimary = Color(0xFF062E63),

    secondary = Color(0xFFADC9F5),
    onSecondary = Color(0xFF17365D),

    background = Color(0xFF101A2A),
    onBackground = Color(0xFFE8F2FF),

    surface = Color(0xFF19263A),
    onSurface = Color(0xFFE8F2FF),

    surfaceVariant = Color(0xFF263A56),
    onSurfaceVariant = Color(0xFFB5C5D9),

    outline = Color(0xFF526783)
)

// Tema principal de la aplicación
@Composable
fun ByteBaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
