package com.example.mimapa.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mimapa.R
import com.example.mimapa.Routes.AdminRoute.AdminDashboard.toAdminDashboard
import com.example.mimapa.Routes.AdminRoute.ManageBusStops.toManageBusStops
import com.example.mimapa.Routes.AdminRoute.ManageRoutes.toManageRoutes
import com.example.mimapa.Routes.AdminRoute.ManageUsers.toManageUsers
import com.example.mimapa.Routes.AdminRoute.ManageVehicles.toManageVehicles
import com.example.mimapa.Routes.AdminRoute.ManageStatistics.toManageStatistics

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
                painter = painterResource(id = R.drawable.rounded_timeline_24),
                contentDescription = "Gestionar lineas"
            )
        }
        IconButton(onClick = {
            navController.toManageBusStops()
        }) {
            Icon(
                imageVector = Icons.Rounded.Place,
                contentDescription = "Gestionar paradas"
            )
        }
        IconButton(onClick = {
            navController.toManageUsers()
        }) {
            Icon(
                imageVector = Icons.Rounded.AccountBox,
                contentDescription = "Gestionar usuarios"
            )
        }
        IconButton(onClick = {
            navController.toManageVehicles()
        }) {
            Icon(
                painter = painterResource(id = R.drawable.round_directions_bus_24),
                contentDescription = "Gestionar vehiculos"
            )
        }
        IconButton(onClick = {
            navController.toManageStatistics()
        }) {
            Icon(
                painter = painterResource(id = R.drawable.round_insert_chart_outlined_24),
                contentDescription = "Estadisticas"
            )
        }
    }
}