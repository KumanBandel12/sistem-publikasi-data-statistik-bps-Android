package com.bps.publikasistatistik.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Welcome : Screen("welcome")
    object Register : Screen("register")
    object Login : Screen("login")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")

    object Detail : Screen("detail/{publicationId}") {
        fun createRoute(id: Long) = "detail/$id"
    }
}