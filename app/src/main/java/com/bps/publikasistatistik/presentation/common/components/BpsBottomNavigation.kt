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
import com.bps.publikasistatistik.presentation.theme.BpsPrimary

@Composable
fun BpsBottomNavigation(
    navController: NavController,
    userRole: String = "user" // Default role
) {
    // 1. List Menu Dasar
    val items = mutableListOf(
        BottomNavItem.Home,
        BottomNavItem.Category,
        BottomNavItem.Search
    )

    // 2. Logika Role: Admin -> Upload, User -> Download
    if (userRole.equals("admin", ignoreCase = true)) {
        items.add(BottomNavItem.Upload)
    } else {
        items.add(BottomNavItem.Download)
    }

    // 3. Tambahkan Profile terakhir
    items.add(BottomNavItem.Profile)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Tampilkan hanya jika route saat ini ada di list items
    val showBottomBar = items.any { it.route == currentRoute }

    if (showBottomBar) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 8.dp
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { Text(item.title, style = MaterialTheme.typography.labelSmall) },
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(BottomNavItem.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BpsPrimary,
                        selectedTextColor = BpsPrimary,
                        indicatorColor = BpsNavigationItem,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    }
}