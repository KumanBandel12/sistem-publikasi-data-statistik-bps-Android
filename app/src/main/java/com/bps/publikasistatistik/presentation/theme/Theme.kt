package com.bps.publikasistatistik.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

// Kita fokus ke Light Theme dulu sesuai desain background putih
private val LightColorScheme = lightColorScheme(
    primary = BpsPrimary,
    onPrimary = BpsOnPrimary,
    primaryContainer = BpsNavigationItem, // Bisa dipakai untuk background chip/nav item
    onPrimaryContainer = BpsPrimary,

    background = BpsBackground,
    onBackground = BpsOnBackground,

    surface = BpsSurface,
    onSurface = BpsOnSurface,

    error = BpsError,
    onError = BpsOnError,

    outline = BpsOutline
)

private val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = Color(0xFF90CAF9), // Biru lebih muda untuk dark mode
    onPrimary = Color(0xFF0D47A1),
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White
    // ... sesuaikan warna lain ...
)

@Composable
fun BPSPublikasiStatistikTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Bisa di-force false jika tidak mau dark mode
    content: @Composable () -> Unit
) {
    // Saat ini kita paksa pakai Light Scheme sesuai desain Anda
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Ubah warna Status Bar (batere/sinyal) jadi putih atau biru
            window.statusBarColor = colorScheme.background.toArgb()
            // Icon status bar jadi gelap (karena background putih)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}