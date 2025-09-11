package com.example.mimapa

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Routes.MainRoute.Login.route) {
        composable(route = Routes.MainRoute.Login.route){
            LoginScreen(navController)
        }
        composable(route = Routes.MainRoute.ForgotPassword.route) {
            ForgotPasswordScreen()
        }
        composable(route = Routes.MainRoute.SignUp.route) {
            SignUpScreen()
        }
        composable(route = Routes.MainRoute.Home.route) {
            HomeScreen()
        }
        composable(route = Routes.MainRoute.Settings.route) {
            SettingsScreen(navController)
        }
    }
}