package com.bps.publikasistatistik.presentation.theme

import androidx.compose.ui.graphics.Color

// Warna Utama (Primary) - #213555
val BpsPrimary = Color(0xFF213555)
val BpsOnPrimary = Color(0xFFFFFFFF) // Teks di atas warna primary (putih)

// Warna Background - #FFFFFF
val BpsBackground = Color(0xFFFFFFFF)
val BpsOnBackground = Color(0xFF213555) // Teks di atas background (biru gelap)

// Warna Surface (untuk Card/Kotak)
val BpsSurface = Color(0xFFFFFFFF)
val BpsOnSurface = Color(0xFF213555)

// Warna Navigasi (Primary dengan transparansi 25%)
// Alpha 0.25 * 255 ≈ 64 -> Hex 40
val BpsNavigationItem = Color(0xFF213555).copy(alpha = 0.25f)
val BpsNavigationSelected = BpsPrimary // Saat dipilih warnanya jadi solid

// Warna Danger/Error - #FF1400
val BpsError = Color(0xFFFF1400)
val BpsOnError = Color(0xFFFFFFFF)

// Warna Tambahan (Opsional, untuk variasi abu-abu teks secondary)
val BpsTextSecondary = Color(0xFF757575)
val BpsOutline = Color(0xFFE0E0E0)