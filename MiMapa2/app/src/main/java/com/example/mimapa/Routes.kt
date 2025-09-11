package com.example.mimapa

import androidx.navigation.NavController

sealed class Routes(val route: String) {

    data object MainRoute : Routes("mainRoutes") {

        data object Login : Routes("${MainRoute.route}/login") {
            fun NavController.toLogin() = navigate("${MainRoute.route}/login")
        }

        data object ForgotPassword : Routes("${MainRoute.route}/forgotPassword") {
            fun NavController.toForgotPassword() = navigate("${MainRoute.route}/forgotPassword")

        }

        data object SignUp : Routes("${MainRoute.route}/signUp") {
            fun NavController.toSignUp() = navigate("${MainRoute.route}/signUp")

        }

        data object Home : Routes("${MainRoute.route}/home") {
            fun NavController.toHome() = navigate("${MainRoute.route}/home")

        }

        data object Settings : Routes("${MainRoute.route}/settings") {
            fun NavController.toSettings() = navigate("${MainRoute.route}/settings")
        }
    }
}