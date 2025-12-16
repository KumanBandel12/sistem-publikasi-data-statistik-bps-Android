package com.bps.publikasistatistik

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bps.publikasistatistik.data.manager.SessionManager
import com.bps.publikasistatistik.presentation.auth.forgot_password.ForgotPasswordScreen
import com.bps.publikasistatistik.presentation.auth.login.LoginScreen
import com.bps.publikasistatistik.presentation.auth.register.RegisterScreen
import com.bps.publikasistatistik.presentation.auth.splash.SplashScreen
import com.bps.publikasistatistik.presentation.auth.welcome.WelcomeScreen
import com.bps.publikasistatistik.presentation.category.CategoryScreen
import com.bps.publikasistatistik.presentation.common.components.BpsBottomNavigation
import com.bps.publikasistatistik.presentation.download.DownloadScreen
import com.bps.publikasistatistik.presentation.download.PdfViewerScreen
import com.bps.publikasistatistik.presentation.home.HomeScreen
import com.bps.publikasistatistik.presentation.navigation.Screen
import com.bps.publikasistatistik.presentation.notification.NotificationScreen
import com.bps.publikasistatistik.presentation.profile.ProfileScreen
import com.bps.publikasistatistik.presentation.profile.change_password.ChangePasswordScreen
import com.bps.publikasistatistik.presentation.profile.edit.EditProfileScreen
import com.bps.publikasistatistik.presentation.profile.faq.FAQScreen
import com.bps.publikasistatistik.presentation.profile.theme.ThemeSelectionScreen
import com.bps.publikasistatistik.presentation.profile.theme.ThemeViewModel
import com.bps.publikasistatistik.presentation.publication.detail.PublicationDetailScreen
import com.bps.publikasistatistik.presentation.search.SearchScreen
import com.bps.publikasistatistik.presentation.theme.BPSPublikasiStatistikTheme
import com.bps.publikasistatistik.data.local.AuthPreferences
import com.bps.publikasistatistik.presentation.publication.edit.EditPublicationScreen
import com.bps.publikasistatistik.presentation.upload.UploadPublicationScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var authPreferences: AuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            val themeState = themeViewModel.currentTheme.collectAsState()

            // 1. Tentukan Theme (Dark/Light/System)
            val isDarkTheme = when (themeState.value) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }

            BPSPublikasiStatistikTheme(darkTheme = isDarkTheme) {
                // Diperlukan agar notifikasi download muncul
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted ->
                            // Izin diberikan/ditolak. DownloadManager akan menyesuaikan.
                        }
                    )
                    // Minta izin saat pertama kali UI dirender
                    SideEffect {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                val navController = rememberNavController()

                val userRoleState = authPreferences.getUserRole().collectAsState(initial = "user")
                val userRole = userRoleState.value ?: "user"

                // Observer Logout Global
                LaunchedEffect(Unit) {
                    sessionManager.logoutEvent.collect { shouldLogout ->
                        if (shouldLogout) {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }

                Scaffold(
                    bottomBar = {
                        BpsBottomNavigation(navController = navController, userRole = userRole)
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

                        composable(Screen.Welcome.route) {
                            WelcomeScreen(navController = navController)
                        }

                        composable(Screen.Register.route) {
                            RegisterScreen(navController = navController)
                        }

                        composable(Screen.Login.route) {
                            // Mengirim navigasi sebagai callback agar LoginScreen lebih bersih
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate(Screen.Register.route)
                                },
                                onNavigateToForgotPassword = {
                                    navController.navigate(Screen.ForgotPassword.route)
                                }
                            )
                        }

                        composable(Screen.ForgotPassword.route) {
                            ForgotPasswordScreen(navController = navController)
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

                        composable(Screen.Detail.route) {
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

                        // Route Upload (Hanya Admin)
                        composable("upload") {
                            UploadPublicationScreen(navController = navController)
                        }

                        composable(
                            route = "edit_publication/{publicationId}",
                            arguments = listOf(navArgument("publicationId") { type = NavType.StringType })
                        ) {
                            EditPublicationScreen(navController = navController)
                        }

                        composable(
                            route = "pdf_viewer/{filePath}",
                            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val filePath = backStackEntry.arguments?.getString("filePath") ?: ""
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