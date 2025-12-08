package com.bps.publikasistatistik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bps.publikasistatistik.presentation.auth.login.LoginScreen
import com.bps.publikasistatistik.presentation.auth.register.RegisterScreen
import com.bps.publikasistatistik.presentation.navigation.Screen
import com.bps.publikasistatistik.presentation.theme.BPSPublikasiStatistikTheme
import com.bps.publikasistatistik.presentation.auth.splash.SplashScreen
import com.bps.publikasistatistik.presentation.category.CategoryScreen
import com.bps.publikasistatistik.presentation.common.components.BpsBottomNavigation
import com.bps.publikasistatistik.presentation.home.HomeScreen
import com.bps.publikasistatistik.presentation.profile.ProfileScreen
import com.bps.publikasistatistik.presentation.profile.change_password.ChangePasswordScreen
import com.bps.publikasistatistik.presentation.profile.edit.EditProfileScreen
import com.bps.publikasistatistik.presentation.profile.faq.FAQScreen
import com.bps.publikasistatistik.presentation.publication.detail.PublicationDetailScreen
import com.bps.publikasistatistik.presentation.search.SearchScreen
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.bps.publikasistatistik.presentation.download.DownloadScreen
import com.bps.publikasistatistik.presentation.download.PdfViewerScreen
import com.bps.publikasistatistik.presentation.notification.NotificationScreen
import com.bps.publikasistatistik.presentation.profile.theme.ThemeSelectionScreen
import com.bps.publikasistatistik.presentation.profile.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            val themeState = themeViewModel.currentTheme.collectAsState()

            // 2. Tentukan apakah pakai Dark Mode atau tidak
            val isDarkTheme = when (themeState.value) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme() // "system" -> ikut settingan HP
            }

            BPSPublikasiStatistikTheme (darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        BpsBottomNavigation(navController = navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Splash.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Splash.route) {
                            SplashScreen(navController = navController)
                        }

                        composable(Screen.Register.route) {
                            RegisterScreen(navController = navController)
                        }

                        composable(Screen.Login.route) {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate(Screen.Register.route)
                                }
                            )
                        }

                        composable(Screen.Home.route) {
                            HomeScreen(
                                navController = navController,
                                onNavigateToDetail = { pubId ->
                                    navController.navigate(Screen.Detail.createRoute(pubId))
                                }
                            )
                        }

                        composable("notifications") {
                            NotificationScreen(navController = navController)
                        }

                        composable(
                            route = Screen.Detail.route,
                            // (Opsional) Validasi tipe argumen agar aman, tapi string default juga oke
                        ) {
                            PublicationDetailScreen(navController = navController)
                        }

                        composable("category") {
                            CategoryScreen(navController = navController)
                        }
                        composable("search") {
                            SearchScreen(navController = navController)
                        }
                        composable("download") {
                            DownloadScreen(navController = navController)
                        }

                        composable(
                            route = "pdf_viewer/{filePath}",
                            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val filePath = backStackEntry.arguments?.getString("filePath") ?: ""

                            // Pass navController ke sini
                            PdfViewerScreen(navController = navController, filePath = filePath)
                        }
                        composable("profile") {
                            ProfileScreen(navController = navController)
                        }
                        composable("profile/edit") {
                            EditProfileScreen(navController = navController)
                        }
                        composable("profile/password") {
                            ChangePasswordScreen(navController = navController)
                        }
                        composable("profile/theme") {
                            ThemeSelectionScreen(navController = navController)
                        }
                        composable("profile/faq") {
                            FAQScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}