package com.bps.publikasistatistik.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Widgets // Ikon untuk Kategori
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(Screen.Home.route, "Beranda", Icons.Default.Home)
    object Category : BottomNavItem("category", "Kategori", Icons.Default.Widgets)
    object Search : BottomNavItem("search", "Cari", Icons.Default.Search)
    object Download : BottomNavItem("download", "Unduhan", Icons.Default.Download)
    object Profile : BottomNavItem("profile", "Profil", Icons.Default.Person)
}