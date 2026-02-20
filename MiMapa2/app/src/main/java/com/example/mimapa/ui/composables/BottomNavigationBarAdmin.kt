package com.example.mimapa.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimapa.Routes.AdminRoute.AdminDashboard.toAdminDashboard
import com.example.mimapa.Routes.AdminRoute.ManageRoutes.toManageRoutes
import com.example.mimapa.Routes.AdminRoute.ManageUsers.toManageUsers
import com.example.mimapa.Routes.AdminRoute.Statistics.toStatistics
import com.example.mimapa.Routes.MainRoute.Favs.toFavoritos
import com.example.mimapa.Routes.MainRoute.History.toHistorial
import com.example.mimapa.Routes.MainRoute.Home.toHome
import com.example.mimapa.Routes.MainRoute.Profile.toPerfil

@Composable
fun BottomNavigationBarAdmin(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround, // O Arrangement.SpaceEvenly
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            navController.toAdminDashboard()
        }) {
            Icon(
                imageVector = Icons.Rounded.Home,
                contentDescription = "Inicio"
            )
        }
        IconButton(onClick = {
            navController.toManageRoutes()
        }) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = "Historial"
            )
        }
        IconButton(onClick = {
            navController.toManageUsers()
        }) {
            Icon(
                imageVector = Icons.Rounded.AccountBox,
                contentDescription = "Favoritos"
            )
        }
        IconButton(onClick = {
            navController.toStatistics()
        }) {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = "Perfil"
            )
        }
    }
}