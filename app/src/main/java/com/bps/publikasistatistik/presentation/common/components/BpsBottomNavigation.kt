package com.bps.publikasistatistik.presentation.common.components

import androidx.compose.foundation.shape.RoundedCornerShape
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
    // Always show 5 tabs for all users (matching new design)
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Category,
        BottomNavItem.Search,
        BottomNavItem.Download,  // Changed to "Tersimpan" (Saved)
        BottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Tampilkan hanya jika route saat ini ada di list items
    val showBottomBar = items.any { it.route == currentRoute }

    if (showBottomBar) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 8.dp,
            modifier = androidx.compose.ui.Modifier
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { 
                        if (isSelected) {
                            Text(item.title, style = MaterialTheme.typography.labelSmall)
                        }
                    },
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