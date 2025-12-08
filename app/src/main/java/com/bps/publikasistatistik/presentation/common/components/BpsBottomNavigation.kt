package com.bps.publikasistatistik.presentation.common.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bps.publikasistatistik.presentation.navigation.BottomNavItem
import com.bps.publikasistatistik.presentation.theme.BpsNavigationItem
import com.bps.publikasistatistik.presentation.theme.BpsNavigationSelected
import com.bps.publikasistatistik.presentation.theme.BpsPrimary

@Composable
fun BpsBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Category,
        BottomNavItem.Search,
        BottomNavItem.Download,
        BottomNavItem.Profile
    )

    // Cek route saat ini agar ikon aktif menyala
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hanya tampilkan BottomBar jika di layar utama (bukan Login/Splash/Detail)
    val showBottomBar = items.any { it.route == currentRoute }

    if (showBottomBar) {
        NavigationBar(
            containerColor = Color.White, // Background putih sesuai desain
            tonalElevation = 8.dp
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // Agar saat back tidak numpuk stack
                                popUpTo(BottomNavItem.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BpsPrimary,
                        selectedTextColor = BpsPrimary,
                        indicatorColor = BpsNavigationItem, // Warna bg ikon saat aktif (biru transparan)
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    }
}