package com.example.mimapa

import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng

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

        data object History : Routes("${MainRoute.route}/history") {
            fun NavController.toHistorial() = navigate("${MainRoute.route}/history")
        }

        data object Favs : Routes("${MainRoute.route}/favs") {
            fun NavController.toFavoritos() = navigate("${MainRoute.route}/favs")
        }

        data object Profile : Routes("${MainRoute.route}/profile") {
            fun NavController.toPerfil() = navigate("${MainRoute.route}/profile")
        }

        data object Credits : Routes("${MainRoute.route}/credits") {
            fun NavController.toCredits() = navigate("${MainRoute.route}/credits")
        }

        data object About : Routes("${MainRoute.route}/about") {
            fun NavController.toAbout() = navigate("${MainRoute.route}/about")
        }

        data object ResetPassword : Routes("${MainRoute.route}/resetPassword") {
            fun NavController.toResetPassword() = navigate("${MainRoute.route}/resetPassword")
        }

        data object DrawRoute : Routes("${MainRoute.route}/drawRoute") {
            fun NavController.toDrawRoute() = navigate("${MainRoute.route}/drawRoute")
        }

        data object FindLocation : Routes("${MainRoute.route}/findLocation") {
            fun NavController.toFindLocation() = navigate("${MainRoute.route}/findLocation")
        }

        data object SetupSearch : Routes("${MainRoute.route}/setupSearch") {
            fun NavController.toSetupSearch() = navigate("${MainRoute.route}/setupSearch")
        }

    }

    // Rutas de administrador
    data object AdminRoute : Routes("adminRoutes") {

        data object AdminDashboard : Routes("${AdminRoute.route}/adminDashboard") {
            fun NavController.toAdminDashboard() = navigate("${AdminRoute.route}/adminDashboard")
        }

        data object ManageUsers : Routes("${AdminRoute.route}/manageUsers") {
            fun NavController.toManageUsers() = navigate("${AdminRoute.route}/manageUsers")
        }

        data object ManageRoutes : Routes("${AdminRoute.route}/manageRoutes") {
            fun NavController.toManageRoutes() = navigate("${AdminRoute.route}/manageRoutes")
        }

        data object Statistics : Routes("${AdminRoute.route}/statistics") {
            fun NavController.toStatistics() = navigate("${AdminRoute.route}/statistics")
        }
    }
}