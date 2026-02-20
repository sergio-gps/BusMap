package com.example.mimapa.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mimapa.Routes

/**
 * Navegación principal de la aplicación.
 */
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Routes.MainRoute.Login.route) {

        composable(route = Routes.MainRoute.Login.route){
            LoginScreen(navController)
        }
        composable(route = Routes.MainRoute.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }
        composable(route = Routes.MainRoute.SignUp.route) {
            SignUpScreen(navController)
        }
        composable(route = Routes.MainRoute.Home.route) {
            HomeScreen(navController)
        }
        composable(route = Routes.MainRoute.Settings.route) {
            SettingsScreen(navController)
        }
        composable(route = Routes.MainRoute.History.route) {
            HistoryScreen(navController)
        }
        composable(route = Routes.MainRoute.Favs.route) {
            FavsScreen(navController)
        }
        composable(route = Routes.MainRoute.Profile.route) {
            ProfileScreen(navController)
        }
        composable(route = Routes.MainRoute.Credits.route) {
            CreditsScreen(navController)
        }
        composable(route = Routes.MainRoute.About.route) {
            AboutScreen(navController)
        }
        composable(route = Routes.MainRoute.ResetPassword.route) {
            ResetPasswordScreen(navController)
        }
        composable(route = Routes.MainRoute.DrawRoute.route) {
            DrawRouteScreen(navController)
        }
        composable(route = Routes.MainRoute.FindLocation.route){
            FindLocationScreen(navController)
        }
        composable(route = Routes.MainRoute.SetupSearch.route){
            SetupSearchScreen(navController)
        }

        // Rutas de administrador
        composable(route = Routes.AdminRoute.AdminDashboard.route) {
            AdminDashboardScreen(navController)
        }
        composable(Routes.AdminRoute.ManageUsers.route) {
            ManageUsersScreen(navController)
        }
        composable(Routes.AdminRoute.ManageRoutes.route) {
            ManageRoutesScreen(navController)
        }
        composable(Routes.AdminRoute.Statistics.route) {
            StatisticsScreen(navController)
        }
        composable(route = Routes.AdminRoute.GenerateAlternativeRoute.route) {
            GenerateAlternativeRouteScreen(navController)
        }
    }
}