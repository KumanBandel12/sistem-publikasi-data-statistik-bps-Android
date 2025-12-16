package com.bps.publikasistatistik.presentation.auth.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bps.publikasistatistik.presentation.navigation.Screen

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isUserLoggedIn = viewModel.isUserLoggedIn.collectAsState().value

    // Observasi status login
    LaunchedEffect(isUserLoggedIn) {
        if (isUserLoggedIn == true) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else if (isUserLoggedIn == false) {
            navController.navigate(Screen.Welcome.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    // UI Sederhana (Logo BPS bisa ditaruh sini nanti)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("BPS Publikasi", style = MaterialTheme.typography.headlineLarge)
        // CircularProgressIndicator() // Opsional
    }
}