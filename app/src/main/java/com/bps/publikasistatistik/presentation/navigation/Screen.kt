package com.bps.publikasistatistik.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Register : Screen("register")
    object Login : Screen("login")
    object Home : Screen("home")

    object Detail : Screen("detail/{publicationId}") {
        fun createRoute(id: Long) = "detail/$id"
    }
}