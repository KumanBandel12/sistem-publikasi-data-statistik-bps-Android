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

// New Design Colors for Home Screen Redesign
val PrimaryBlue = Color(0xFF1E3A5F)
val NavyDark = Color(0xFF2C3E50)
val AccentOrange = Color(0xFFFF6B35)
val TextPrimary = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF6B7280)
val TextTertiary = Color(0xFF9CA3AF)
val BackgroundCard = Color(0xFFFFFFFF)
val BackgroundScreen = Color(0xFFF9FAFB)
val TagBackground = Color(0xFFEEF2FF)
val TagText = Color(0xFF4F46E5)

// Featured Publications (Orange gradient)
val FeaturedLabelStartColor = Color(0xFFFBBB49)
val FeaturedLabelEndColor = Color(0xFFED9E36)

// Regular Publications (Grey-Navy gradient)
val SubCategoryLabelStartColor = Color(0xFF64748B)
val SubCategoryLabelEndColor = Color(0xFF213555)